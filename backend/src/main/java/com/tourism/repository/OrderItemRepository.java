package com.tourism.repository;

import com.tourism.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
            SELECT oi FROM OrderItem oi
            JOIN TourOrder o ON oi.orderId = o.id
            WHERE o.userId = :userId AND o.status IN ('PAID','COMPLETED')
            """)
    List<OrderItem> findPaidItemsByUser(@Param("userId") Long userId);

    @Query("""
            SELECT o.userId, oi.productType, oi.productId FROM OrderItem oi
            JOIN TourOrder o ON oi.orderId = o.id
            WHERE o.status IN ('PAID','COMPLETED')
            """)
    List<Object[]> findPaidUserProductPairs();
}
