package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyApplicationCreateRequestDto(
    @NotBlank(message = "{validation.application.motivation.required}")
    @Size(min = 2, max = 500, message = "{validation.application.motivation.length}")
    String motivation
) {
  public String normalizedMotivation() {
    return motivation.trim();
  }
}
