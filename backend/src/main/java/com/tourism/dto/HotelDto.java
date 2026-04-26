package com.tourism.dto;

import java.math.BigDecimal;

public record HotelDto(
        Long id,
        String name,
        String description,
        String city,
        Long categoryId,
        BigDecimal pricePerNight,
        String imageUrl,
        String keywordTags,
        boolean active,
        Double avgRating
) {
}
