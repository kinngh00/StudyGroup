package StudyGroup.StudyGroup.domain.study.dto.response;

import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;

public record StudyCreateResponseDto(
    Long studyGroupId,
    String name,
    String description,
    Integer maxMemberCount,
    String status
) {
  public static StudyCreateResponseDto from(StudyGroup studyGroup) {
    return new StudyCreateResponseDto(
        studyGroup.getId(),
        studyGroup.getName(),
        studyGroup.getDescription(),
        studyGroup.getMaxMemberCount(),
        studyGroup.getStatus().name()
    );
  }
}
