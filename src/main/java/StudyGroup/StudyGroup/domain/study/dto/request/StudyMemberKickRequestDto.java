package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.Size;

public record StudyMemberKickRequestDto(
    boolean addToBlacklist,
    @Size(max = 500, message = "{validation.reason.max}")
    String reason
) {
  public String normalizedReason() {
    if (reason == null) {
      return null;
    }
    return reason.trim();
  }
}
