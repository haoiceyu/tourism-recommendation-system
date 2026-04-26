package com.tourism.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 64)
    private String city;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "price_per_night", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerNight;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(name = "keyword_tags", length = 512)
    private String keywordTags;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
