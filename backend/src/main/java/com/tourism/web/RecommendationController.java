package com.tourism.web;

import com.tourism.dto.AttractionDto;
import com.tourism.dto.HotelDto;
import com.tourism.repository.AttractionRepository;
import com.tourism.repository.HotelRepository;
import com.tourism.security.SecurityUtil;
import com.tourism.service.ProductMapper;
import com.tourism.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final AttractionRepository attractionRepository;
    private final HotelRepository hotelRepository;
    private final ProductMapper productMapper;

    @GetMapping("/attractions")
    public List<AttractionDto> attractions(@RequestParam(defaultValue = "10") int limit) {
        List<Long> ids = recommendationService.recommendAttractions(SecurityUtil.currentUser().id(), limit);
        return mapAttractions(ids);
    }

    @GetMapping("/hotels")
    public List<HotelDto> hotels(@RequestParam(defaultValue = "10") int limit) {
        List<Long> ids = recommendationService.recommendHotels(SecurityUtil.currentUser().id(), limit);
        return mapHotels(ids);
    }

    @GetMapping("/mixed")
    public Map<String, Object> mixed(@RequestParam(defaultValue = "6") int limit) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("attractions", mapAttractions(recommendationService.recommendAttractions(SecurityUtil.currentUser().id(), limit)));
        m.put("hotels", mapHotels(recommendationService.recommendHotels(SecurityUtil.currentUser().id(), limit)));
        return m;
    }

    private List<AttractionDto> mapAttractions(List<Long> ids) {
        Map<Long, com.tourism.entity.Attraction> map = attractionRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(com.tourism.entity.Attraction::getId, a -> a));
        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(productMapper::toAttractionDto)
                .collect(Collectors.toList());
    }

    private List<HotelDto> mapHotels(List<Long> ids) {
        Map<Long, com.tourism.entity.Hotel> map = hotelRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(com.tourism.entity.Hotel::getId, h -> h));
        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(productMapper::toHotelDto)
                .collect(Collectors.toList());
    }
}
