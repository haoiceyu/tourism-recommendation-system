package com.tourism.dto;

import com.tourism.domain.UserRole;

import java.time.Instant;

public record UserProfileDto(
        Long id,
        String username,
        String email,
        String nickname,
        String phone,
        UserRole role,
        Instant createdAt
) {
}
