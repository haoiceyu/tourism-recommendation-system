package com.tourism.entity;

import com.tourism.domain.BehaviorAction;
import com.tourism.domain.ProductType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_behaviors", indexes = {
        @Index(name = "idx_behavior_user", columnList = "user_id"),
        @Index(name = "idx_behavior_product", columnList = "product_type, product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, length = 16)
    private ProductType productType;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private BehaviorAction action;

    @Column(nullable = false)
    private double weight;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
