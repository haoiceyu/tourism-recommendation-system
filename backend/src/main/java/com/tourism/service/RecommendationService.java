package com.tourism.service;

import com.tourism.config.TourismProperties;
import com.tourism.domain.ProductType;
import com.tourism.entity.*;
import com.tourism.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final String KEY_PREFIX = "rec:v1:";

    private final StringRedisTemplate redis;
    private final TourismProperties props;
    private final UserBehaviorRepository behaviorRepository;
    private final FavoriteRepository favoriteRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final AttractionRepository attractionRepository;
    private final HotelRepository hotelRepository;

    public List<Long> recommendAttractions(Long userId, int limit) {
        return recommend(ProductType.ATTRACTION, userId, limit, () -> attractionRepository.findAll().stream()
                .filter(Attraction::isActive)
                .map(Attraction::getId)
                .collect(Collectors.toList()));
    }

    public List<Long> recommendHotels(Long userId, int limit) {
        return recommend(ProductType.HOTEL, userId, limit, () -> hotelRepository.findAll().stream()
                .filter(Hotel::isActive)
                .map(Hotel::getId)
                .collect(Collectors.toList()));
    }

    public void evictUserCache(Long userId) {
        redis.delete(KEY_PREFIX + userId + ":ATTRACTION");
        redis.delete(KEY_PREFIX + userId + ":HOTEL");
    }

    private List<Long> recommend(ProductType type, Long userId, int limit, java.util.function.Supplier<List<Long>> allIds) {
        String key = KEY_PREFIX + userId + ":" + type.name();
        String cached = redis.opsForValue().get(key);
        if (cached != null && !cached.isBlank()) {
            return Arrays.stream(cached.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        Map<Long, Map<String, Double>> userVectors = buildUserVectors();
        Map<Long, Double> cfScores = collaborativeScores(userId, type, userVectors);
        Map<Long, Double> cbScores = contentScores(userId, type);
        List<Long> ids = allIds.get();
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<Long, Double> hybrid = new HashMap<>();
        for (Long pid : ids) {
            double cf = normalize(cfScores, pid);
            double cb = normalize(cbScores, pid);
            hybrid.put(pid, props.getRecommendation().getCfWeight() * cf + props.getRecommendation().getCbWeight() * cb);
        }

        List<Long> sorted = hybrid.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        double maxHybrid = hybrid.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        if (sorted.isEmpty() || maxHybrid < 1e-9) {
            sorted = popularityFallback(type, ids, limit);
        } else {
            sorted = sorted.stream().limit(limit).collect(Collectors.toList());
        }

        if (sorted.size() < limit) {
            LinkedHashSet<Long> merged = new LinkedHashSet<>(sorted);
            for (Long id : popularityFallback(type, ids, limit)) {
                if (merged.size() >= limit) break;
                merged.add(id);
            }
            sorted = new ArrayList<>(merged);
        }

        String joined = sorted.stream().map(String::valueOf).collect(Collectors.joining(","));
        redis.opsForValue().set(key, joined, Duration.ofSeconds(props.getRecommendation().getCacheTtlSeconds()));
        return sorted.stream().limit(limit).collect(Collectors.toList());
    }

    private static double normalize(Map<Long, Double> m, Long id) {
        if (m.isEmpty()) return 0d;
        double min = m.values().stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = m.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
        double v = m.getOrDefault(id, 0d);
        if (max - min < 1e-9) return v > 0 ? 1d : 0d;
        return (v - min) / (max - min);
    }

    private Map<Long, Map<String, Double>> buildUserVectors() {
        Map<Long, Map<String, Double>> map = new HashMap<>();
        for (UserBehavior b : behaviorRepository.findAll()) {
            String key = itemKey(b.getProductType(), b.getProductId());
            map.computeIfAbsent(b.getUserId(), u -> new HashMap<>())
                    .merge(key, b.getWeight(), Double::sum);
        }
        for (Favorite f : favoriteRepository.findAll()) {
            String key = itemKey(f.getProductType(), f.getProductId());
            map.computeIfAbsent(f.getUserId(), u -> new HashMap<>()).merge(key, 3.0, Double::sum);
        }
        for (Object[] row : orderItemRepository.findPaidUserProductPairs()) {
            Long uid = (Long) row[0];
            ProductType pt = (ProductType) row[1];
            Long pid = (Long) row[2];
            String key = itemKey(pt, pid);
            map.computeIfAbsent(uid, u -> new HashMap<>()).merge(key, 5.0, Double::sum);
        }
        return map;
    }

    private Map<Long, Double> collaborativeScores(Long userId, ProductType type, Map<Long, Map<String, Double>> userVectors) {
        Map<String, Double> target = userVectors.getOrDefault(userId, Map.of());
        Map<Long, Double> scores = new HashMap<>();
        if (target.isEmpty()) {
            return scores;
        }
        for (Map.Entry<Long, Map<String, Double>> e : userVectors.entrySet()) {
            if (e.getKey().equals(userId)) continue;
            double sim = cosine(target, e.getValue());
            if (sim <= 0) continue;
            for (Map.Entry<String, Double> it : e.getValue().entrySet()) {
                if (!it.getKey().startsWith(type.name() + ":")) continue;
                if (target.containsKey(it.getKey())) continue;
                Long pid = Long.parseLong(it.getKey().substring(type.name().length() + 1));
                scores.merge(pid, sim * it.getValue(), Double::sum);
            }
        }
        return scores;
    }

    private static double cosine(Map<String, Double> a, Map<String, Double> b) {
        Set<String> keys = new HashSet<>();
        keys.addAll(a.keySet());
        keys.addAll(b.keySet());
        double dot = 0, na = 0, nb = 0;
        for (String k : keys) {
            double va = a.getOrDefault(k, 0d);
            double vb = b.getOrDefault(k, 0d);
            dot += va * vb;
            na += va * va;
            nb += vb * vb;
        }
        if (na < 1e-12 || nb < 1e-12) return 0d;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    private Map<Long, Double> contentScores(Long userId, ProductType type) {
        Map<Long, Double> catWeight = new HashMap<>();
        Map<String, Double> tagWeight = new HashMap<>();
        aggregateProfile(userId, type, catWeight, tagWeight);

        Map<Long, Double> scores = new HashMap<>();
        if (type == ProductType.ATTRACTION) {
            for (Attraction a : attractionRepository.findAll()) {
                if (!a.isActive()) continue;
                double s = scoreItem(a.getCategoryId(), a.getKeywordTags(), catWeight, tagWeight);
                scores.put(a.getId(), s);
            }
        } else {
            for (Hotel h : hotelRepository.findAll()) {
                if (!h.isActive()) continue;
                double s = scoreItem(h.getCategoryId(), h.getKeywordTags(), catWeight, tagWeight);
                scores.put(h.getId(), s);
            }
        }
        return scores;
    }

    private void aggregateProfile(Long userId, ProductType type, Map<Long, Double> catWeight, Map<String, Double> tagWeight) {
        for (UserBehavior b : behaviorRepository.findByUserId(userId)) {
            if (b.getProductType() != type) continue;
            addItemFeatures(b.getProductType(), b.getProductId(), b.getWeight(), catWeight, tagWeight);
        }
        for (Favorite f : favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId)) {
            if (f.getProductType() != type) continue;
            addItemFeatures(f.getProductType(), f.getProductId(), 3.0, catWeight, tagWeight);
        }
        for (OrderItem oi : orderItemRepository.findPaidItemsByUser(userId)) {
            if (oi.getProductType() != type) continue;
            addItemFeatures(oi.getProductType(), oi.getProductId(), 5.0, catWeight, tagWeight);
        }
        for (Review r : reviewRepository.findByUserId(userId)) {
            if (r.getProductType() != type) continue;
            double w = r.getRating() / 5.0 * 2.0;
            addItemFeatures(r.getProductType(), r.getProductId(), w, catWeight, tagWeight);
        }
    }

    private void addItemFeatures(ProductType type, Long productId, double w, Map<Long, Double> catWeight, Map<String, Double> tagWeight) {
        if (type == ProductType.ATTRACTION) {
            attractionRepository.findById(productId).ifPresent(a -> {
                if (a.getCategoryId() != null) {
                    catWeight.merge(a.getCategoryId(), w, Double::sum);
                }
                mergeTags(a.getKeywordTags(), w, tagWeight);
            });
        } else {
            hotelRepository.findById(productId).ifPresent(h -> {
                if (h.getCategoryId() != null) {
                    catWeight.merge(h.getCategoryId(), w, Double::sum);
                }
                mergeTags(h.getKeywordTags(), w, tagWeight);
            });
        }
    }

    private static void mergeTags(String tags, double w, Map<String, Double> tagWeight) {
        if (tags == null || tags.isBlank()) return;
        for (String t : tags.split("[,，\\s]+")) {
            if (t.isBlank()) continue;
            tagWeight.merge(t.trim().toLowerCase(Locale.ROOT), w, Double::sum);
        }
    }

    private static double scoreItem(Long categoryId, String tags, Map<Long, Double> catWeight, Map<String, Double> tagWeight) {
        double s = 0;
        if (categoryId != null) {
            s += catWeight.getOrDefault(categoryId, 0d);
        }
        if (tags != null && !tags.isBlank()) {
            for (String t : tags.split("[,，\\s]+")) {
                if (t.isBlank()) continue;
                s += tagWeight.getOrDefault(t.trim().toLowerCase(Locale.ROOT), 0d);
            }
        }
        return s;
    }

    private List<Long> popularityFallback(ProductType type, Collection<Long> candidateIds, int limit) {
        Map<Long, Double> pop = new HashMap<>();
        for (Long id : candidateIds) pop.put(id, 0d);
        for (Favorite f : favoriteRepository.findAll()) {
            if (f.getProductType() != type) continue;
            pop.merge(f.getProductId(), 2.0, Double::sum);
        }
        for (Object[] row : orderItemRepository.findPaidUserProductPairs()) {
            ProductType pt = (ProductType) row[1];
            Long pid = (Long) row[2];
            if (pt != type) continue;
            pop.merge(pid, 3.0, Double::sum);
        }
        for (Review r : reviewRepository.findAll()) {
            if (r.getProductType() != type) continue;
            pop.merge(r.getProductId(), r.getRating() * 0.5, Double::sum);
        }
        return pop.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private static String itemKey(ProductType type, Long id) {
        return type.name() + ":" + id;
    }
}
