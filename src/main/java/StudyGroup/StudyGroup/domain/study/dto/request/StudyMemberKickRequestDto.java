package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.Size;

public record StudyMemberKickRequestDto(
    boolean addToBlacklist,
    @Size(max = 500, message = "������ 500�� ���Ͽ��� �մϴ�.")
    String reason
) {
  public String normalizedReason() {
    if (reason == null) {
      return null;
    }
    return reason.trim();
  }
}
