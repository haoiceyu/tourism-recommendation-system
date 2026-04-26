package com.tourism.web;

import com.tourism.domain.ProductType;
import com.tourism.entity.Favorite;
import com.tourism.security.SecurityUtil;
import com.tourism.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public List<Favorite> list() {
        return favoriteService.list(SecurityUtil.currentUser().id());
    }

    @PostMapping
    public void add(@RequestParam ProductType productType, @RequestParam Long productId) {
        favoriteService.add(SecurityUtil.currentUser().id(), productType, productId);
    }

    @DeleteMapping
    public void remove(@RequestParam ProductType productType, @RequestParam Long productId) {
        favoriteService.remove(SecurityUtil.currentUser().id(), productType, productId);
    }
}
