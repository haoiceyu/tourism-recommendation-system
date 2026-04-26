package com.tourism.repository;

import com.tourism.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("""
            SELECT h FROM Hotel h WHERE h.active = true
            AND (:categoryId IS NULL OR h.categoryId = :categoryId)
            AND (:keyword IS NULL OR :keyword = ''
                OR LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(h.city) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(h.keywordTags, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Hotel> search(@Param("keyword") String keyword,
                       @Param("categoryId") Long categoryId,
                       Pageable pageable);
}
