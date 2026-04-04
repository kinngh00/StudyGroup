package StudyGroup.StudyGroup.domain.study.dto.response;

import StudyGroup.StudyGroup.domain.study.entity.StudyApplication;
import java.time.LocalDateTime;

public record StudyApplicationResponseDto(
    Long applicationId,
    Long recruitmentPostId,
    Long studyGroupId,
    Long applicantUserId,
    String applicantName,
    String motivation,
    String status,
    LocalDateTime decidedAt,
    LocalDateTime createdAt
) {
  public static StudyApplicationResponseDto from(StudyApplication studyApplication) {
    return new StudyApplicationResponseDto(
        studyApplication.getId(),
        studyApplication.getRecruitmentPost().getId(),
        studyApplication.getStudyGroup().getId(),
        studyApplication.getApplicantUser().getId(),
        studyApplication.getApplicantUser().getName(),
        studyApplication.getMotivation(),
        studyApplication.getStatus().name(),
        studyApplication.getDecidedAt(),
        studyApplication.getCreatedAt()
    );
  }
}
