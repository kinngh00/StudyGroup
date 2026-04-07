package StudyGroup.StudyGroup.domain.user.dto.response;

import StudyGroup.StudyGroup.domain.user.entity.User;

public record UserMeResponseDto(
    Long userId,
    String email,
    String name,
    String role,
    String provider
) {
  public static UserMeResponseDto from(User user) {
    return new UserMeResponseDto(
        user.getId(),
        user.getEmail(),
        user.getName(),
        user.getRole().name(),
        user.getProvider().name()
    );
  }
}

