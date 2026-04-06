package StudyGroup.StudyGroup.domain.study.dto.request;

import StudyGroup.StudyGroup.domain.study.entity.StudyStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyUpdateRequestDto(
    @NotBlank(message = "Study name is required.")
    @Size(min = 2, max = 100, message = "Study name must be between 2 and 100 characters.")
    String name,

    @NotBlank(message = "Study description is required.")
    @Size(min = 2, max = 1000, message = "Study description must be between 2 and 1000 characters.")
    String description,

    @Min(value = 2, message = "Max member count must be at least 2.")
    @Max(value = 100, message = "Max member count must be 100 or less.")
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
