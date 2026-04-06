package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyApplicationCreateRequestDto(
    @NotBlank(message = "���� ����� �ʼ��Դϴ�.")
    @Size(min = 2, max = 500, message = "���� ����� 2�� �̻� 500�� ���Ͽ��� �մϴ�.")
    String motivation
) {
  public String normalizedMotivation() {
    return motivation.trim();
  }
}
