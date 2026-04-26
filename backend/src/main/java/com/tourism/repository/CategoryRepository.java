package com.tourism.repository;

import com.tourism.domain.ProductType;
import com.tourism.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByScope(ProductType scope);
}
