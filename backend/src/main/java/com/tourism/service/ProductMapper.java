package com.tourism.service;

import com.tourism.domain.ProductType;
import com.tourism.dto.AttractionDto;
import com.tourism.dto.HotelDto;
import com.tourism.entity.Attraction;
import com.tourism.entity.Hotel;
import com.tourism.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ReviewRepository reviewRepository;

    public AttractionDto toAttractionDto(Attraction a) {
        Double avg = reviewRepository.averageRating(ProductType.ATTRACTION, a.getId());
        return new AttractionDto(
                a.getId(),
                a.getName(),
                a.getDescription(),
                a.getCity(),
                a.getCategoryId(),
                a.getPrice(),
                a.getImageUrl(),
                a.getKeywordTags(),
                a.isActive(),
                avg
        );
    }

    public HotelDto toHotelDto(Hotel h) {
        Double avg = reviewRepository.averageRating(ProductType.HOTEL, h.getId());
        return new HotelDto(
                h.getId(),
                h.getName(),
                h.getDescription(),
                h.getCity(),
                h.getCategoryId(),
                h.getPricePerNight(),
                h.getImageUrl(),
                h.getKeywordTags(),
                h.isActive(),
                avg
        );
    }
}
