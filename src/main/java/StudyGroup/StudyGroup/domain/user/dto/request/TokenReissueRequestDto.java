package StudyGroup.StudyGroup.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenReissueRequestDto(
    @NotBlank(message = "{validation.refresh-token.required}")
    String refreshToken
) {
}
