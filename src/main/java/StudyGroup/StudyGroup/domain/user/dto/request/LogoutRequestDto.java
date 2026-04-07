package StudyGroup.StudyGroup.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequestDto(
    @NotBlank(message = "{validation.refresh-token.required}")
    String refreshToken
) {
}
