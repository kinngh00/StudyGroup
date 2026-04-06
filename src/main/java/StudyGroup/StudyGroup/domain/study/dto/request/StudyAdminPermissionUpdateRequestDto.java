package StudyGroup.StudyGroup.domain.study.dto.request;

public record StudyAdminPermissionUpdateRequestDto(
    boolean recruitmentWrite,
    boolean recruitmentApprove,
    boolean memberManage,
    boolean scheduleManage,
    boolean noticeManage
) {
}
