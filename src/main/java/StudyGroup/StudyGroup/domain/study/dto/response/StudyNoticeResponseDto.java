package StudyGroup.StudyGroup.domain.study.dto.response;

import StudyGroup.StudyGroup.domain.study.entity.StudyNotice;
import java.time.LocalDateTime;

public record StudyNoticeResponseDto(
    Long studyNoticeId,
    Long studyGroupId,
    Long authorUserId,
    String authorName,
    String title,
    String content,
    boolean pinned,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static StudyNoticeResponseDto from(StudyNotice studyNotice) {
    return new StudyNoticeResponseDto(
        studyNotice.getId(),
        studyNotice.getStudyGroup().getId(),
        studyNotice.getAuthorUser().getId(),
        studyNotice.getAuthorUser().getName(),
        studyNotice.getTitle(),
        studyNotice.getContent(),
        studyNotice.isPinned(),
        studyNotice.getCreatedAt(),
        studyNotice.getUpdatedAt()
    );
  }
}

