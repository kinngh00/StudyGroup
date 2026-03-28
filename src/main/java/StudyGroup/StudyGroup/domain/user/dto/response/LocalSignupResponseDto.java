package StudyGroup.StudyGroup.domain.user.dto.response;

import StudyGroup.StudyGroup.domain.user.entity.User;

public record LocalSignupResponseDto(
    String email,
    String name
) {
  public LocalSignupResponseDto(User user) {
    this(user.getEmail(), user.getName());
  }
}

