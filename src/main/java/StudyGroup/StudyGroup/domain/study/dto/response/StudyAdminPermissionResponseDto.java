package StudyGroup.StudyGroup.domain.study.dto.response;

import StudyGroup.StudyGroup.domain.study.entity.StudyAdminPermission;

public record StudyAdminPermissionResponseDto(
    Long userId,
    boolean recruitmentWrite,
    boolean recruitmentApprove,
    boolean memberManage,
    boolean scheduleManage,
    boolean noticeManage
) {
  public static StudyAdminPermissionResponseDto from(StudyAdminPermission studyAdminPermission) {
    return new StudyAdminPermissionResponseDto(
        studyAdminPermission.getUser().getId(),
        studyAdminPermission.isRecruitmentWrite(),
        studyAdminPermission.isRecruitmentApprove(),
        studyAdminPermission.isMemberManage(),
        studyAdminPermission.isScheduleManage(),
        studyAdminPermission.isNoticeManage()
    );
  }
}
