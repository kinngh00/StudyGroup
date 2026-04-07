package StudyGroup.StudyGroup.domain.study.dto.response;

import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;

public record StudySummaryResponseDto(
    Long studyGroupId,
    String name,
    Integer maxMemberCount,
    String status
) {
  public static StudySummaryResponseDto from(StudyGroup studyGroup) {
    return new StudySummaryResponseDto(
        studyGroup.getId(),
        studyGroup.getName(),
        studyGroup.getMaxMemberCount(),
        studyGroup.getStatus().name()
    );
  }
}
