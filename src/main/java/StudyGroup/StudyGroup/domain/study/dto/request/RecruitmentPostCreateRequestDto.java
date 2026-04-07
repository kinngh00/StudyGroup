package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecruitmentPostCreateRequestDto(
    @NotBlank(message = "{validation.recruitment.title.required}")
    @Size(min = 2, max = 100, message = "{validation.recruitment.title.length}")
    String title,

    @NotBlank(message = "{validation.recruitment.content.required}")
    @Size(min = 2, max = 2000, message = "{validation.recruitment.content.length}")
    String content
) {
  public String normalizedTitle() {
    return title.trim();
  }

  public String normalizedContent() {
    return content.trim();
  }
}
