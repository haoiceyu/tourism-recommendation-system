package com.tourism.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductUpsertRequest(
        @NotBlank String name,
        String description,
        String city,
        Long categoryId,
        @NotNull BigDecimal price,
        String imageUrl,
        String keywordTags,
        @NotNull Boolean active
) {
}
