package com.example.study.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenReissueRequestDto(
    @NotBlank(message = "Refresh token is required.")
    String refreshToken
) {
}
