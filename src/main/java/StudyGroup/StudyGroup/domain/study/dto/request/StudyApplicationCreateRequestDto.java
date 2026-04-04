package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyApplicationCreateRequestDto(
    @NotBlank(message = "지원 동기는 필수입니다.")
    @Size(min = 2, max = 500, message = "지원 동기는 2자 이상 500자 이하여야 합니다.")
    String motivation
) {
  public String normalizedMotivation() {
    return motivation.trim();
  }
}