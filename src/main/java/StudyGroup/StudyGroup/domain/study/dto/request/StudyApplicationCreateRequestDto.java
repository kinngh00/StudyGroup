package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyApplicationCreateRequestDto(
    @NotBlank(message = "Application motivation is required.")
    @Size(min = 2, max = 500, message = "Application motivation must be between 2 and 500 characters.")
    String motivation
) {
  public String normalizedMotivation() {
    return motivation.trim();
  }
}
