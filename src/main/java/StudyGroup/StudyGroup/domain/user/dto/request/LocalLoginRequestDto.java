package StudyGroup.StudyGroup.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record LocalLoginRequestDto(
    @NotBlank(message = "�̸����� �ʼ��Դϴ�.")
    @Email(message = "�̸��� ������ �ùٸ��� �ʽ��ϴ�.")
    String email,

    @NotBlank(message = "��й�ȣ�� �ʼ��Դϴ�.")
    @Size(min = 8, max = 50, message = "��й�ȣ�� 8�� �̻� 50�� ���Ͽ��� �մϴ�.")
    String password
) {
  public String normalizedEmail() {
    return email.trim().toLowerCase(Locale.ROOT);
  }
}
