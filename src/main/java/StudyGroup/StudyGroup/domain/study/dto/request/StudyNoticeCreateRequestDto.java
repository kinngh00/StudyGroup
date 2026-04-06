package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyNoticeCreateRequestDto(
    @NotBlank(message = "Notice title is required.")
    @Size(min = 2, max = 100, message = "Notice title must be between 2 and 100 characters.")
    String title,

    @NotBlank(message = "Notice content is required.")
    @Size(min = 2, max = 2000, message = "Notice content must be between 2 and 2000 characters.")
    String content,

    boolean pinned
) {
  public String normalizedTitle() {
    return title.trim();
  }

  public String normalizedContent() {
    return content.trim();
  }
}
