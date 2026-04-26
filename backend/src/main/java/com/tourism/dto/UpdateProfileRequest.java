package com.tourism.dto;

import jakarta.validation.constraints.Email;

public record UpdateProfileRequest(
        String nickname,
        @Email String email,
        String phone
) {
}
