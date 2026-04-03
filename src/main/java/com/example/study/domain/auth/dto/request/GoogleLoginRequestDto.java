package com.example.study.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequestDto(
    @NotBlank(message = "Google id token is required.")
    String idToken
) {
}
