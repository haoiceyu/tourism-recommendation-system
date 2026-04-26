package com.tourism.dto;

import com.tourism.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDto(
        Long id,
        String orderNo,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt,
        Instant paidAt,
        Instant completedAt,
        Instant cancelledAt,
        List<OrderItemDto> items
) {
}
