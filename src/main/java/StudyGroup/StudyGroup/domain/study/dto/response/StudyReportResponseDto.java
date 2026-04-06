package StudyGroup.StudyGroup.domain.study.dto.response;

import StudyGroup.StudyGroup.domain.study.entity.StudyReport;
import StudyGroup.StudyGroup.domain.study.entity.StudyReportStatus;
import java.time.LocalDateTime;

public record StudyReportResponseDto(
    Long studyReportId,
    Long studyGroupId,
    Long reporterUserId,
    String reporterName,
    Long targetUserId,
    String targetUserName,
    String content,
    StudyReportStatus status,
    Long resolverUserId,
    String resolverName,
    LocalDateTime resolvedAt,
    LocalDateTime createdAt
) {
  public static StudyReportResponseDto from(StudyReport studyReport) {
    return new StudyReportResponseDto(
        studyReport.getId(),
        studyReport.getStudyGroup().getId(),
        studyReport.getReporterUser().getId(),
        studyReport.getReporterUser().getName(),
        studyReport.getTargetUser() == null ? null : studyReport.getTargetUser().getId(),
        studyReport.getTargetUser() == null ? null : studyReport.getTargetUser().getName(),
        studyReport.getContent(),
        studyReport.getStatus(),
        studyReport.getResolverUser() == null ? null : studyReport.getResolverUser().getId(),
        studyReport.getResolverUser() == null ? null : studyReport.getResolverUser().getName(),
        studyReport.getResolvedAt(),
        studyReport.getCreatedAt()
    );
  }
}

