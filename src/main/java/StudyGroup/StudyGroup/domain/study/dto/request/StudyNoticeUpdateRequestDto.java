package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyNoticeUpdateRequestDto(
    @NotBlank(message = "{validation.notice.title.required}")
    @Size(min = 2, max = 100, message = "{validation.notice.title.length}")
    String title,

    @NotBlank(message = "{validation.notice.content.required}")
    @Size(min = 2, max = 2000, message = "{validation.notice.content.length}")
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
