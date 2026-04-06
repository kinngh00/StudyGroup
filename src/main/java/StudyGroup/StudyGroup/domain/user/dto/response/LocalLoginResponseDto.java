package StudyGroup.StudyGroup.domain.user.dto.response;

import StudyGroup.StudyGroup.domain.user.entity.User;

public record LocalLoginResponseDto(
    String email,
    String name,
    String accessToken,
    String refreshToken
) {
  public LocalLoginResponseDto(User user, String accessToken, String refreshToken) {
    this(user.getEmail(), user.getName(), accessToken, refreshToken);
  }
}

