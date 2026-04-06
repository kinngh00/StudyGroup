package StudyGroup.StudyGroup.domain.study.dto.response;

import StudyGroup.StudyGroup.domain.study.entity.StudyAdminPermission;
import StudyGroup.StudyGroup.domain.study.entity.StudyMember;

public record StudyMemberResponseDto(
    Long userId,
    String userName,
    String role,
    boolean recruitmentWrite,
    boolean recruitmentApprove,
    boolean memberManage,
    boolean scheduleManage,
    boolean noticeManage
) {
  public static StudyMemberResponseDto from(StudyMember studyMember, StudyAdminPermission studyAdminPermission) {
    boolean isOwner = "OWNER".equals(studyMember.getRole().name());
    return new StudyMemberResponseDto(
        studyMember.getUser().getId(),
        studyMember.getUser().getName(),
        studyMember.getRole().name(),
        isOwner || (studyAdminPermission != null && studyAdminPermission.isRecruitmentWrite()),
        isOwner || (studyAdminPermission != null && studyAdminPermission.isRecruitmentApprove()),
        isOwner || (studyAdminPermission != null && studyAdminPermission.isMemberManage()),
        isOwner || (studyAdminPermission != null && studyAdminPermission.isScheduleManage()),
        isOwner || (studyAdminPermission != null && studyAdminPermission.isNoticeManage())
    );
  }
}

