package com.tourism.repository;

import com.tourism.entity.Attraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {

    @Query("""
            SELECT a FROM Attraction a WHERE a.active = true
            AND (:categoryId IS NULL OR a.categoryId = :categoryId)
            AND (:keyword IS NULL OR :keyword = ''
                OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.city) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(a.keywordTags, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Attraction> search(@Param("keyword") String keyword,
                            @Param("categoryId") Long categoryId,
                            Pageable pageable);
}
