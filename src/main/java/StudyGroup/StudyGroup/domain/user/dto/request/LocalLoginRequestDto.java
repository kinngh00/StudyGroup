package StudyGroup.StudyGroup.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record LocalLoginRequestDto(
    @NotBlank(message = "Email is required.")
    @Email(message = "Email format is invalid.")
    String email,

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters.")
    String password
) {
  public String normalizedEmail() {
    return email.trim().toLowerCase(Locale.ROOT);
  }
}
