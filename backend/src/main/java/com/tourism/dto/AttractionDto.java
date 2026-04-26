package com.tourism.dto;

import java.math.BigDecimal;

public record AttractionDto(
        Long id,
        String name,
        String description,
        String city,
        Long categoryId,
        BigDecimal price,
        String imageUrl,
        String keywordTags,
        boolean active,
        Double avgRating
) {
}
