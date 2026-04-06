package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyReportCreateRequestDto(
    Long targetUserId,

    @NotBlank(message = "신고 내용은 필수입니다.")
    @Size(min = 2, max = 1000, message = "신고 내용은 2자 이상 1000자 이하여야 합니다.")
    String content
) {
  public String normalizedContent() {
    return content.trim();
  }
}

