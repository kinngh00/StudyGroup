package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotNull;

public record StudyAdminAssignRequestDto(
    @NotNull(message = "�����ڷ� ������ ����� ID�� �ʼ��Դϴ�.")
    Long userId,
    boolean recruitmentWrite,
    boolean recruitmentApprove,
    boolean memberManage,
    boolean scheduleManage,
    boolean noticeManage
) {
}
