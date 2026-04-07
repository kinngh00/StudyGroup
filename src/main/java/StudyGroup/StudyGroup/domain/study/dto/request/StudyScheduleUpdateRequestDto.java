package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record StudyScheduleUpdateRequestDto(
    @NotBlank(message = "{validation.schedule.title.required}")
    @Size(min = 2, max = 100, message = "{validation.schedule.title.length}")
    String title,

    @NotBlank(message = "{validation.schedule.description.required}")
    @Size(min = 2, max = 1000, message = "{validation.schedule.description.length}")
    String description,

    @NotNull(message = "{validation.schedule.time.required}")
    @Future(message = "{validation.schedule.time.future}")
    LocalDateTime scheduledAt
) {
  public String normalizedTitle() {
    return title.trim();
  }

  public String normalizedDescription() {
    return description.trim();
  }
}
