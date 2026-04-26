package com.tourism.dto;

import com.tourism.domain.ProductType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull ProductType productType,
        @NotNull Long productId,
        @Min(1) @Max(5) int rating,
        String content
) {
}
