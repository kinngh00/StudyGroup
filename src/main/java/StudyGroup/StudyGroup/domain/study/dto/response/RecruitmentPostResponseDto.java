package StudyGroup.StudyGroup.domain.study.dto.response;

import StudyGroup.StudyGroup.domain.study.entity.RecruitmentPost;
import java.time.LocalDateTime;

public record RecruitmentPostResponseDto(
    Long recruitmentPostId,
    Long studyGroupId,
    Long authorUserId,
    String title,
    String content,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static RecruitmentPostResponseDto from(RecruitmentPost recruitmentPost) {
    return new RecruitmentPostResponseDto(
        recruitmentPost.getId(),
        recruitmentPost.getStudyGroup().getId(),
        recruitmentPost.getAuthorUser().getId(),
        recruitmentPost.getTitle(),
        recruitmentPost.getContent(),
        recruitmentPost.getStatus().name(),
        recruitmentPost.getCreatedAt(),
        recruitmentPost.getUpdatedAt()
    );
  }
}
