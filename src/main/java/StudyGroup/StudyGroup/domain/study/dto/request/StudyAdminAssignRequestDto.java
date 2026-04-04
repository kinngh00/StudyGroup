package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.NotNull;

public record StudyAdminAssignRequestDto(
    @NotNull(message = "관리자로 지정할 사용자 ID는 필수입니다.")
    Long userId
) {
}
