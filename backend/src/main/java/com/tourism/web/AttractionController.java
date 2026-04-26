package com.tourism.web;

import com.tourism.dto.AttractionDto;
import com.tourism.exception.BadRequestException;
import com.tourism.repository.AttractionRepository;
import com.tourism.service.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attractions")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionRepository attractionRepository;
    private final ProductMapper productMapper;

    @GetMapping
    public Page<AttractionDto> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 12) Pageable pageable) {
        return attractionRepository.search(
                keyword == null ? "" : keyword,
                categoryId,
                pageable
        ).map(productMapper::toAttractionDto);
    }

    @GetMapping("/{id}")
    public AttractionDto detail(@PathVariable Long id) {
        return attractionRepository.findById(id)
                .map(productMapper::toAttractionDto)
                .orElseThrow(() -> new BadRequestException("景点不存在"));
    }
}
