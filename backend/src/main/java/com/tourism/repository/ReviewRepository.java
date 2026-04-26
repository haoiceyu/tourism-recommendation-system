package com.tourism.repository;

import com.tourism.domain.ProductType;
import com.tourism.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProductTypeAndProductIdOrderByCreatedAtDesc(ProductType productType, Long productId, Pageable pageable);

    Optional<Review> findByUserIdAndProductTypeAndProductId(Long userId, ProductType productType, Long productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productType = :t AND r.productId = :pid")
    Double averageRating(@Param("t") ProductType t, @Param("pid") Long productId);

    List<Review> findByUserId(Long userId);
}
