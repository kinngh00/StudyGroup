package StudyGroup.StudyGroup.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record LocalSignupRequestDto(
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하로 입력해야 합니다.")
    String password,

    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해야 합니다.")
    String name
) {
  public LocalSignupRequestDto {
    email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    name = name == null ? null : name.trim();
  }
}
