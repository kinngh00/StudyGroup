package com.example.study.domain.auth.dto.response;

public record AuthTokenResponseDto(
    String accessToken,
    String refreshToken
) {
}
