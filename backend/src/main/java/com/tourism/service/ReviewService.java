package com.tourism.service;

import com.tourism.domain.ProductType;
import com.tourism.dto.ReviewDto;
import com.tourism.dto.ReviewRequest;
import com.tourism.entity.Review;
import com.tourism.entity.User;
import com.tourism.exception.BadRequestException;
import com.tourism.repository.ReviewRepository;
import com.tourism.repository.TourOrderRepository;
import com.tourism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TourOrderRepository tourOrderRepository;
    private final UserRepository userRepository;
    private final RecommendationService recommendationService;

    public Page<ReviewDto> list(ProductType type, Long productId, Pageable pageable) {
        return reviewRepository.findByProductTypeAndProductIdOrderByCreatedAtDesc(type, productId, pageable)
                .map(this::toDto);
    }

    @Transactional
    public ReviewDto create(Long userId, ReviewRequest req) {
        if (!tourOrderRepository.userHasConsumed(userId, req.productType(), req.productId())) {
            throw new BadRequestException("仅可对已消费的订单产品评价");
        }
        if (reviewRepository.findByUserIdAndProductTypeAndProductId(userId, req.productType(), req.productId()).isPresent()) {
            throw new BadRequestException("您已评价过该产品");
        }
        Review r = Review.builder()
                .userId(userId)
                .productType(req.productType())
                .productId(req.productId())
                .rating(req.rating())
                .content(req.content())
                .createdAt(Instant.now())
                .build();
        r = reviewRepository.save(r);
        recommendationService.evictUserCache(userId);
        return toDto(r);
    }

    private ReviewDto toDto(Review r) {
        User u = userRepository.findById(r.getUserId()).orElseThrow();
        return new ReviewDto(
                r.getId(),
                r.getUserId(),
                u.getUsername(),
                r.getProductType(),
                r.getProductId(),
                r.getRating(),
                r.getContent(),
                r.getCreatedAt()
        );
    }
}
