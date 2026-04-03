package com.example.study.domain.auth.dto.response;

import com.example.study.domain.user.entity.User;

public record LoginResponseDto(
    Long userId,
    String email,
    String name,
    String accessToken,
    String refreshToken
) {

  public static LoginResponseDto of(User user, AuthTokenResponseDto authTokenResponseDto) {
    return new LoginResponseDto(
        user.getId(),
        user.getEmail(),
        user.getName(),
        authTokenResponseDto.accessToken(),
        authTokenResponseDto.refreshToken()
    );
  }
}
