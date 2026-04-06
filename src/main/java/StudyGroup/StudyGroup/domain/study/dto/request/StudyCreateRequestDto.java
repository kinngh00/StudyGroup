package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyCreateRequestDto(
    @NotBlank(message = "���͵� �̸��� �ʼ��Դϴ�.")
    @Size(min = 2, max = 100, message = "���͵� �̸��� 2�� �̻� 100�� ���Ͽ��� �մϴ�.")
    String name,

    @NotBlank(message = "���͵� ������ �ʼ��Դϴ�.")
    @Size(min = 2, max = 1000, message = "���͵� ������ 2�� �̻� 1000�� ���Ͽ��� �մϴ�.")
    String description,

    @Min(value = 2, message = "�ִ� �ο��� 2�� �̻��̾�� �մϴ�.")
    @Max(value = 100, message = "�ִ� �ο��� 100�� ���Ͽ��� �մϴ�.")
    Integer maxMemberCount
) {
  public String normalizedName() {
    return name.trim();
  }

  public String normalizedDescription() {
    return description.trim();
  }
}
