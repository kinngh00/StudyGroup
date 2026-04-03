package com.example.study.domain.auth.dto.response;

import com.example.study.domain.user.entity.User;

public record MeResponseDto(
    Long userId,
    String email,
    String name,
    String role
) {

  public static MeResponseDto from(User user) {
    return new MeResponseDto(user.getId(), user.getEmail(), user.getName(), user.getRole().name());
  }
}
