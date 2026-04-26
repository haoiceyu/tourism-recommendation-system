package com.tourism.dto;

import com.tourism.domain.ProductType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderLineRequest(
        @NotNull ProductType productType,
        @NotNull Long productId,
        @Min(1) int quantity
) {
}
