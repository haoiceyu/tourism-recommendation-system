package com.tourism.dto;

import com.tourism.domain.ProductType;

public record CategoryDto(Long id, String name, ProductType scope) {
}
