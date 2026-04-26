package com.tourism.web;

import com.tourism.dto.HotelDto;
import com.tourism.exception.BadRequestException;
import com.tourism.repository.HotelRepository;
import com.tourism.service.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelRepository hotelRepository;
    private final ProductMapper productMapper;

    @GetMapping
    public Page<HotelDto> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 12) Pageable pageable) {
        return hotelRepository.search(
                keyword == null ? "" : keyword,
                categoryId,
                pageable
        ).map(productMapper::toHotelDto);
    }

    @GetMapping("/{id}")
    public HotelDto detail(@PathVariable Long id) {
        return hotelRepository.findById(id)
                .map(productMapper::toHotelDto)
                .orElseThrow(() -> new BadRequestException("酒店不存在"));
    }
}
