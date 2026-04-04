package StudyGroup.StudyGroup.domain.study.dto.request;

import StudyGroup.StudyGroup.domain.study.entity.RecruitmentPostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecruitmentPostUpdateRequestDto(
    @NotBlank(message = "모집글 제목은 필수입니다.")
    @Size(min = 2, max = 100, message = "모집글 제목은 2자 이상 100자 이하여야 합니다.")
    String title,

    @NotBlank(message = "모집글 내용은 필수입니다.")
    @Size(min = 2, max = 2000, message = "모집글 내용은 2자 이상 2000자 이하여야 합니다.")
    String content,

    RecruitmentPostStatus status
) {
  public String normalizedTitle() {
    return title.trim();
  }

  public String normalizedContent() {
    return content.trim();
  }
}