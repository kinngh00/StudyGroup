package StudyGroup.StudyGroup.domain.study.dto.response;

import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;

public record StudyDetailResponseDto(
    Long studyGroupId,
    String name,
    String description,
    Integer maxMemberCount,
    Long currentMemberCount,
    String status,
    Long ownerUserId,
    String ownerName
) {
  public static StudyDetailResponseDto from(
      StudyGroup studyGroup,
      long currentMemberCount,
      Long ownerUserId,
      String ownerName
  ) {
    return new StudyDetailResponseDto(
        studyGroup.getId(),
        studyGroup.getName(),
        studyGroup.getDescription(),
        studyGroup.getMaxMemberCount(),
        currentMemberCount,
        studyGroup.getStatus().name(),
        ownerUserId,
        ownerName
    );
  }
}
