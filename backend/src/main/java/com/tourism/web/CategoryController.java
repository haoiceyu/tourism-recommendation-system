package com.tourism.web;

import com.tourism.domain.ProductType;
import com.tourism.dto.CategoryDto;
import com.tourism.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<CategoryDto> list(@RequestParam(required = false) ProductType scope) {
        var list = scope == null ? categoryRepository.findAll() : categoryRepository.findByScope(scope);
        return list.stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getScope()))
                .collect(Collectors.toList());
    }
}
