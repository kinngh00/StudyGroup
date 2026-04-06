package StudyGroup.StudyGroup.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record LocalSignupRequestDto(
    @NotBlank(message = "Email is required.")
    @Email(message = "Email format is invalid.")
    String email,

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters.")
    String password,

    @NotBlank(message = "Name is required.")
    @Size(min = 2, max = 20, message = "Name must be between 2 and 20 characters.")
    String name
) {
  public String normalizedEmail() {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  public String normalizedName() {
    return name.trim();
  }
}
