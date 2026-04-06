package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecruitmentPostCreateRequestDto(
    @NotBlank(message = "Recruitment title is required.")
    @Size(min = 2, max = 100, message = "Recruitment title must be between 2 and 100 characters.")
    String title,

    @NotBlank(message = "Recruitment content is required.")
    @Size(min = 2, max = 2000, message = "Recruitment content must be between 2 and 2000 characters.")
    String content
) {
  public String normalizedTitle() {
    return title.trim();
  }

  public String normalizedContent() {
    return content.trim();
  }
}
