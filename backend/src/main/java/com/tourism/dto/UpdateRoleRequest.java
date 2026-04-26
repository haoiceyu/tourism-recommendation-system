package com.tourism.dto;

import com.tourism.domain.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(@NotNull UserRole role) {
}
