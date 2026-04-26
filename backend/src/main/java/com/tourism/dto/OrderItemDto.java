package com.tourism.dto;

import com.tourism.domain.ProductType;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        ProductType productType,
        Long productId,
        String title,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal subtotal
) {
}
