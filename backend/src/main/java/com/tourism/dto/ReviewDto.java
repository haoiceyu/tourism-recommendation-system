package com.tourism.dto;

import com.tourism.domain.ProductType;

import java.time.Instant;

public record ReviewDto(
        Long id,
        Long userId,
        String username,
        ProductType productType,
        Long productId,
        int rating,
        String content,
        Instant createdAt
) {
}
