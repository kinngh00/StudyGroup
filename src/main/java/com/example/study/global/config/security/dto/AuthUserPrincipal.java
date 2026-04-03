package com.example.study.global.config.security.dto;

import com.example.study.domain.user.entity.UserRole;

public record AuthUserPrincipal(
    Long userId,
    String email,
    UserRole userRole
) {
}
