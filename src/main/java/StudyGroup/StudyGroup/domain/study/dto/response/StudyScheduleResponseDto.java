package StudyGroup.StudyGroup.domain.study.dto.response;

import StudyGroup.StudyGroup.domain.study.entity.StudySchedule;
import java.time.LocalDateTime;

public record StudyScheduleResponseDto(
    Long studyScheduleId,
    Long studyGroupId,
    String title,
    String description,
    LocalDateTime scheduledAt
) {
  public static StudyScheduleResponseDto from(StudySchedule studySchedule) {
    return new StudyScheduleResponseDto(
        studySchedule.getId(),
        studySchedule.getStudyGroup().getId(),
        studySchedule.getTitle(),
        studySchedule.getDescription(),
        studySchedule.getScheduledAt()
    );
  }
}
