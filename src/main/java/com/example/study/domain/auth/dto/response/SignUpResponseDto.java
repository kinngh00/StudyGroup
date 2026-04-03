package com.example.study.domain.auth.dto.response;

import com.example.study.domain.user.entity.User;

public record SignUpResponseDto(
    Long userId,
    String email,
    String name
) {

  public static SignUpResponseDto from(User user) {
    return new SignUpResponseDto(user.getId(), user.getEmail(), user.getName());
  }
}
