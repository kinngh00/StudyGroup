package StudyGroup.StudyGroup.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record LocalLoginRequestDto(
    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    String email,

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 8, max = 50, message = "{validation.password.length}")
    String password
) {
  public String normalizedEmail() {
    return email.trim().toLowerCase(Locale.ROOT);
  }
}
