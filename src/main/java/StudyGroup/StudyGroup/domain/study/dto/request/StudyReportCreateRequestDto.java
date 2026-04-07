package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyReportCreateRequestDto(
    Long targetUserId,

    @NotBlank(message = "{validation.report.content.required}")
    @Size(min = 2, max = 1000, message = "{validation.report.content.length}")
    String content
) {
  public String normalizedContent() {
    return content.trim();
  }
}
