package StudyGroup.StudyGroup.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record LocalSignupRequestDto(
    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    String email,

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 8, max = 50, message = "{validation.password.length}")
    String password,

    @NotBlank(message = "{validation.name.required}")
    @Size(min = 2, max = 20, message = "{validation.name.length}")
    String name
) {
  public String normalizedEmail() {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  public String normalizedName() {
    return name.trim();
  }
}
