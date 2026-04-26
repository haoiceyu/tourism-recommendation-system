package com.tourism.web;

import com.tourism.dto.AttractionDto;
import com.tourism.dto.HotelDto;
import com.tourism.dto.ProductUpsertRequest;
import com.tourism.entity.Attraction;
import com.tourism.entity.Hotel;
import com.tourism.exception.BadRequestException;
import com.tourism.repository.AttractionRepository;
import com.tourism.repository.HotelRepository;
import com.tourism.service.ProductMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminProductController {

    private final AttractionRepository attractionRepository;
    private final HotelRepository hotelRepository;
    private final ProductMapper productMapper;

    @GetMapping("/attractions")
    public List<AttractionDto> listAttractions() {
        return attractionRepository.findAll().stream()
                .map(productMapper::toAttractionDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/attractions")
    public AttractionDto createAttraction(@Valid @RequestBody ProductUpsertRequest req) {
        Attraction a = Attraction.builder()
                .name(req.name())
                .description(req.description())
                .city(req.city())
                .categoryId(req.categoryId())
                .price(req.price())
                .imageUrl(req.imageUrl())
                .keywordTags(req.keywordTags())
                .active(req.active())
                .build();
        a = attractionRepository.save(a);
        return productMapper.toAttractionDto(a);
    }

    @PutMapping("/attractions/{id}")
    public AttractionDto updateAttraction(@PathVariable Long id, @Valid @RequestBody ProductUpsertRequest req) {
        Attraction a = attractionRepository.findById(id).orElseThrow(() -> new BadRequestException("景点不存在"));
        a.setName(req.name());
        a.setDescription(req.description());
        a.setCity(req.city());
        a.setCategoryId(req.categoryId());
        a.setPrice(req.price());
        a.setImageUrl(req.imageUrl());
        a.setKeywordTags(req.keywordTags());
        a.setActive(req.active());
        attractionRepository.save(a);
        return productMapper.toAttractionDto(a);
    }

    @DeleteMapping("/attractions/{id}")
    public void deleteAttraction(@PathVariable Long id) {
        if (!attractionRepository.existsById(id)) {
            throw new BadRequestException("景点不存在");
        }
        attractionRepository.deleteById(id);
    }

    @GetMapping("/hotels")
    public List<HotelDto> listHotels() {
        return hotelRepository.findAll().stream()
                .map(productMapper::toHotelDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/hotels")
    public HotelDto createHotel(@Valid @RequestBody ProductUpsertRequest req) {
        Hotel h = Hotel.builder()
                .name(req.name())
                .description(req.description())
                .city(req.city())
                .categoryId(req.categoryId())
                .pricePerNight(req.price())
                .imageUrl(req.imageUrl())
                .keywordTags(req.keywordTags())
                .active(req.active())
                .build();
        h = hotelRepository.save(h);
        return productMapper.toHotelDto(h);
    }

    @PutMapping("/hotels/{id}")
    public HotelDto updateHotel(@PathVariable Long id, @Valid @RequestBody ProductUpsertRequest req) {
        Hotel h = hotelRepository.findById(id).orElseThrow(() -> new BadRequestException("酒店不存在"));
        h.setName(req.name());
        h.setDescription(req.description());
        h.setCity(req.city());
        h.setCategoryId(req.categoryId());
        h.setPricePerNight(req.price());
        h.setImageUrl(req.imageUrl());
        h.setKeywordTags(req.keywordTags());
        h.setActive(req.active());
        hotelRepository.save(h);
        return productMapper.toHotelDto(h);
    }

    @DeleteMapping("/hotels/{id}")
    public void deleteHotel(@PathVariable Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new BadRequestException("酒店不存在");
        }
        hotelRepository.deleteById(id);
    }
}
