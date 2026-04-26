package com.tourism.repository;

import com.tourism.domain.OrderStatus;
import com.tourism.domain.ProductType;
import com.tourism.entity.TourOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TourOrderRepository extends JpaRepository<TourOrder, Long> {
    Optional<TourOrder> findByOrderNo(String orderNo);

    List<TourOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM TourOrder o WHERE o.status IN ('PAID','COMPLETED') AND o.paidAt >= ?1")
    BigDecimal sumPaidAmountSince(Instant since);

    @Query("""
            SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END FROM OrderItem oi
            JOIN TourOrder o ON oi.orderId = o.id
            WHERE o.userId = :uid AND o.status IN ('PAID','COMPLETED')
            AND oi.productType = :t AND oi.productId = :pid
            """)
    boolean userHasConsumed(@Param("uid") Long userId, @Param("t") ProductType t, @Param("pid") Long productId);
}
