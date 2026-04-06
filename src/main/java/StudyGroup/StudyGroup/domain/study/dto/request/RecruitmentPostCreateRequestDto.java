package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecruitmentPostCreateRequestDto(
    @NotBlank(message = "������ ������ �ʼ��Դϴ�.")
    @Size(min = 2, max = 100, message = "������ ������ 2�� �̻� 100�� ���Ͽ��� �մϴ�.")
    String title,

    @NotBlank(message = "������ ������ �ʼ��Դϴ�.")
    @Size(min = 2, max = 2000, message = "������ ������ 2�� �̻� 2000�� ���Ͽ��� �մϴ�.")
    String content
) {
  public String normalizedTitle() {
    return title.trim();
  }

  public String normalizedContent() {
    return content.trim();
  }
}
