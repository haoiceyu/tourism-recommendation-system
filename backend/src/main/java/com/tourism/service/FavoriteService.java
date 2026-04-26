package com.tourism.service;

import com.tourism.domain.ProductType;
import com.tourism.entity.Favorite;
import com.tourism.exception.BadRequestException;
import com.tourism.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RecommendationService recommendationService;

    public List<Favorite> list(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void add(Long userId, ProductType type, Long productId) {
        if (favoriteRepository.existsByUserIdAndProductTypeAndProductId(userId, type, productId)) {
            throw new BadRequestException("已收藏");
        }
        Favorite f = Favorite.builder()
                .userId(userId)
                .productType(type)
                .productId(productId)
                .createdAt(Instant.now())
                .build();
        favoriteRepository.save(f);
        recommendationService.evictUserCache(userId);
    }

    @Transactional
    public void remove(Long userId, ProductType type, Long productId) {
        favoriteRepository.findByUserIdAndProductTypeAndProductId(userId, type, productId)
                .ifPresent(favoriteRepository::delete);
        recommendationService.evictUserCache(userId);
    }
}
