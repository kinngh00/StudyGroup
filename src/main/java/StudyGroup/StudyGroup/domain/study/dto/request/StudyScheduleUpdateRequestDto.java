package StudyGroup.StudyGroup.domain.study.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record StudyScheduleUpdateRequestDto(
    @NotBlank(message = "Schedule title is required.")
    @Size(min = 2, max = 100, message = "Schedule title must be between 2 and 100 characters.")
    String title,

    @NotBlank(message = "Schedule description is required.")
    @Size(min = 2, max = 1000, message = "Schedule description must be between 2 and 1000 characters.")
    String description,

    @NotNull(message = "Schedule time is required.")
    @Future(message = "Schedule time must be in the future.")
    LocalDateTime scheduledAt
) {
  public String normalizedTitle() {
    return title.trim();
  }

  public String normalizedDescription() {
    return description.trim();
  }
}
