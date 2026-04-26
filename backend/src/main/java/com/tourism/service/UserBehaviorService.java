package com.tourism.service;

import com.tourism.domain.BehaviorAction;
import com.tourism.domain.ProductType;
import com.tourism.entity.UserBehavior;
import com.tourism.repository.UserBehaviorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserBehaviorService {

    private static final double VIEW_WEIGHT = 1.0;

    private final UserBehaviorRepository behaviorRepository;
    private final RecommendationService recommendationService;

    @Transactional
    public void recordView(Long userId, ProductType type, Long productId) {
        UserBehavior b = UserBehavior.builder()
                .userId(userId)
                .productType(type)
                .productId(productId)
                .action(BehaviorAction.VIEW)
                .weight(VIEW_WEIGHT)
                .createdAt(Instant.now())
                .build();
        behaviorRepository.save(b);
        recommendationService.evictUserCache(userId);
    }
}
