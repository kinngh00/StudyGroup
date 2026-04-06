package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record StudyScheduleUpdateRequestDto(
    @NotBlank(message = "���� ������ �ʼ��Դϴ�.")
    @Size(min = 2, max = 100, message = "���� ������ 2�� �̻� 100�� ���Ͽ��� �մϴ�.")
    String title,

    @NotBlank(message = "���� ������ �ʼ��Դϴ�.")
    @Size(min = 2, max = 1000, message = "���� ������ 2�� �̻� 1000�� ���Ͽ��� �մϴ�.")
    String description,

    @NotNull(message = "���� �ð��� �ʼ��Դϴ�.")
    @Future(message = "���� �ð��� ���� �ð� ���Ŀ��� �մϴ�.")
    LocalDateTime scheduledAt
) {
  public String normalizedTitle() {
    return title.trim();
  }

  public String normalizedDescription() {
    return description.trim();
  }
}
