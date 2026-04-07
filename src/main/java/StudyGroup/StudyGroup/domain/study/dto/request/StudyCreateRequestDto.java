package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyCreateRequestDto(
    @NotBlank(message = "{validation.study.name.required}")
    @Size(min = 2, max = 100, message = "{validation.study.name.length}")
    String name,

    @NotBlank(message = "{validation.study.description.required}")
    @Size(min = 2, max = 1000, message = "{validation.study.description.length}")
    String description,

    @Min(value = 2, message = "{validation.study.max-member-count.min}")
    @Max(value = 100, message = "{validation.study.max-member-count.max}")
    Integer maxMemberCount
) {
  public String normalizedName() {
    return name.trim();
  }

  public String normalizedDescription() {
    return description.trim();
  }
}
