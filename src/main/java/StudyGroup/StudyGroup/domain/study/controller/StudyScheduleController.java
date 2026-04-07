package StudyGroup.StudyGroup.domain.study.controller;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyScheduleCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyScheduleUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyScheduleResponseDto;
import StudyGroup.StudyGroup.domain.study.service.StudyScheduleService;
import StudyGroup.StudyGroup.global.auth.AuthenticatedUserPrincipal;
import StudyGroup.StudyGroup.global.response.ApiResponseDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyGroupId}/schedules")
public class StudyScheduleController {

  private final StudyScheduleService studyScheduleService;

  @PostMapping
  public ResponseEntity<ApiResponseDto<StudyScheduleResponseDto>> createSchedule(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @RequestBody @Valid StudyScheduleCreateRequestDto studyScheduleCreateRequestDto
  ) {
    StudyScheduleResponseDto studyScheduleResponseDto = studyScheduleService.createSchedule(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        studyScheduleCreateRequestDto
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "Study schedule created.", studyScheduleResponseDto));
  }

  @GetMapping
  public ResponseEntity<ApiResponseDto<List<StudyScheduleResponseDto>>> getSchedules(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId
  ) {
    List<StudyScheduleResponseDto> studyScheduleResponseDtoList = studyScheduleService.getSchedules(
        authenticatedUserPrincipal.userId(),
        studyGroupId
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study schedule list retrieved.", studyScheduleResponseDtoList));
  }

  @PatchMapping("/{studyScheduleId}")
  public ResponseEntity<ApiResponseDto<StudyScheduleResponseDto>> updateSchedule(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long studyScheduleId,
      @RequestBody @Valid StudyScheduleUpdateRequestDto studyScheduleUpdateRequestDto
  ) {
    StudyScheduleResponseDto studyScheduleResponseDto = studyScheduleService.updateSchedule(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        studyScheduleId,
        studyScheduleUpdateRequestDto
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study schedule updated.", studyScheduleResponseDto));
  }

  @DeleteMapping("/{studyScheduleId}")
  public ResponseEntity<ApiResponseDto<Void>> deleteSchedule(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long studyScheduleId
  ) {
    studyScheduleService.deleteSchedule(authenticatedUserPrincipal.userId(), studyGroupId, studyScheduleId);
    return ResponseEntity.ok(ApiResponseDto.success("Study schedule deleted.", null));
  }
}
