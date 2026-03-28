package StudyGroup.StudyGroup.domain.user.dto.response;

import StudyGroup.StudyGroup.domain.user.entity.User;

public record LocalLoginResponseDto(
    String email,
    String name,
    String accessToken
) {
  public LocalLoginResponseDto(User user, String accessToken) {
    this(user.getEmail(), user.getName(), accessToken);
  }
}

