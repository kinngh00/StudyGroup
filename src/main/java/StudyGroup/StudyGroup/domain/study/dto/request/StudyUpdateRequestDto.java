package StudyGroup.StudyGroup.domain.study.dto.request;

import StudyGroup.StudyGroup.domain.study.entity.StudyStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyUpdateRequestDto(
    @NotBlank(message = "스터디 이름은 필수입니다.")
    @Size(min = 2, max = 100, message = "스터디 이름은 2자 이상 100자 이하여야 합니다.")
    String name,

    @NotBlank(message = "스터디 설명은 필수입니다.")
    @Size(min = 2, max = 1000, message = "스터디 설명은 2자 이상 1000자 이하여야 합니다.")
    String description,

    @Min(value = 2, message = "최대 인원은 2명 이상이어야 합니다.")
    @Max(value = 100, message = "최대 인원은 100명 이하여야 합니다.")
    Integer maxMemberCount,

    StudyStatus status
) {
  public String normalizedName() {
    return name.trim();
  }

  public String normalizedDescription() {
    return description.trim();
  }
}