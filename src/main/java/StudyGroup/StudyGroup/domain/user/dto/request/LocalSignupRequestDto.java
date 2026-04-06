package StudyGroup.StudyGroup.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record LocalSignupRequestDto(
    @NotBlank(message = "�̸����� �ʼ��Դϴ�.")
    @Email(message = "�̸��� ������ �ùٸ��� �ʽ��ϴ�.")
    String email,

    @NotBlank(message = "��й�ȣ�� �ʼ��Դϴ�.")
    @Size(min = 8, max = 50, message = "��й�ȣ�� 8�� �̻� 50�� ���Ͽ��� �մϴ�.")
    String password,

    @NotBlank(message = "�̸��� �ʼ��Դϴ�.")
    @Size(min = 2, max = 20, message = "�̸��� 2�� �̻� 20�� ���Ͽ��� �մϴ�.")
    String name
) {
  public String normalizedEmail() {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  public String normalizedName() {
    return name.trim();
  }
}
