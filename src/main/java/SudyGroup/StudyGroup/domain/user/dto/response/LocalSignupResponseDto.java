package SudyGroup.StudyGroup.domain.user.dto.response;

import SudyGroup.StudyGroup.domain.user.entity.User;

public record LocalSignupResponseDto(
    String email,
    String name
) {
  public LocalSignupResponseDto(User user) {
    this(user.getEmail(), user.getName());
  }
}
