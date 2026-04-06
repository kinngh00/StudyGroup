package StudyGroup.StudyGroup.domain.study.dto.response;

import StudyGroup.StudyGroup.domain.study.entity.StudyRestriction;
import java.time.LocalDateTime;

public record StudyRestrictionResponseDto(
    Long userId,
    String userName,
    boolean blacklisted,
    boolean blocked,
    String reason,
    LocalDateTime blacklistedAt,
    LocalDateTime blockedAt
) {
  public static StudyRestrictionResponseDto from(StudyRestriction studyRestriction) {
    return new StudyRestrictionResponseDto(
        studyRestriction.getUser().getId(),
        studyRestriction.getUser().getName(),
        studyRestriction.isBlacklisted(),
        studyRestriction.isBlocked(),
        studyRestriction.getReason(),
        studyRestriction.getBlacklistedAt(),
        studyRestriction.getBlockedAt()
    );
  }
}
