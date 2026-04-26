package com.tourism.dto;

import java.math.BigDecimal;

public record DashboardStatsDto(
        long totalUsers,
        long newUsersToday,
        long pendingPaymentOrders,
        long paidOrders,
        long completedOrders,
        long cancelledOrders,
        BigDecimal revenueToday
) {
}
