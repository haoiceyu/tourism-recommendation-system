package com.tourism.web;

import com.tourism.domain.ProductType;
import com.tourism.dto.ReviewDto;
import com.tourism.dto.ReviewRequest;
import com.tourism.security.SecurityUtil;
import com.tourism.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public Page<ReviewDto> list(
            @RequestParam ProductType productType,
            @RequestParam Long productId,
            @PageableDefault(size = 10) Pageable pageable) {
        return reviewService.list(productType, productId, pageable);
    }

    @PostMapping
    public ReviewDto create(@Valid @RequestBody ReviewRequest req) {
        return reviewService.create(SecurityUtil.currentUser().id(), req);
    }
}
