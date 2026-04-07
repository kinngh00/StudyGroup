package StudyGroup.StudyGroup.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequestDto(
    @NotBlank(message = "{validation.google.id-token.required}")
    String idToken
) {
}
