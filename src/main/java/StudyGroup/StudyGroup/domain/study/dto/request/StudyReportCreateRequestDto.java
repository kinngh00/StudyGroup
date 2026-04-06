package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyReportCreateRequestDto(
    Long targetUserId,

    @NotBlank(message = "Report content is required.")
    @Size(min = 2, max = 1000, message = "Report content must be between 2 and 1000 characters.")
    String content
) {
  public String normalizedContent() {
    return content.trim();
  }
}
