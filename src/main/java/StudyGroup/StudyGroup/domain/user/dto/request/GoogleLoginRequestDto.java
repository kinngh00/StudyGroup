package StudyGroup.StudyGroup.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequestDto(
    @NotBlank(message = "Google id token은 필수입니다.")
    String idToken
) {
}
