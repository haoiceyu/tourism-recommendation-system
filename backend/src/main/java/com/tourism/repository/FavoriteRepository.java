package com.tourism.repository;

import com.tourism.domain.ProductType;
import com.tourism.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Favorite> findByUserIdAndProductTypeAndProductId(Long userId, ProductType productType, Long productId);

    boolean existsByUserIdAndProductTypeAndProductId(Long userId, ProductType productType, Long productId);
}
