package StudyGroup.StudyGroup.domain.study.controller;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyNoticeCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyNoticeUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyNoticeResponseDto;
import StudyGroup.StudyGroup.domain.study.service.StudyNoticeService;
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
@RequestMapping("/api/studies/{studyGroupId}/notices")
public class StudyNoticeController {

  private final StudyNoticeService studyNoticeService;

  @PostMapping
  public ResponseEntity<ApiResponseDto<StudyNoticeResponseDto>> createNotice(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @RequestBody @Valid StudyNoticeCreateRequestDto studyNoticeCreateRequestDto
  ) {
    StudyNoticeResponseDto studyNoticeResponseDto = studyNoticeService.createNotice(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        studyNoticeCreateRequestDto
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "Study notice created.", studyNoticeResponseDto));
  }

  @GetMapping
  public ResponseEntity<ApiResponseDto<List<StudyNoticeResponseDto>>> getNotices(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId
  ) {
    List<StudyNoticeResponseDto> studyNoticeResponseDtoList = studyNoticeService.getNotices(
        authenticatedUserPrincipal.userId(),
        studyGroupId
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study notice list retrieved.", studyNoticeResponseDtoList));
  }

  @GetMapping("/{studyNoticeId}")
  public ResponseEntity<ApiResponseDto<StudyNoticeResponseDto>> getNotice(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long studyNoticeId
  ) {
    StudyNoticeResponseDto studyNoticeResponseDto = studyNoticeService.getNotice(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        studyNoticeId
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study notice retrieved.", studyNoticeResponseDto));
  }

  @PatchMapping("/{studyNoticeId}")
  public ResponseEntity<ApiResponseDto<StudyNoticeResponseDto>> updateNotice(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long studyNoticeId,
      @RequestBody @Valid StudyNoticeUpdateRequestDto studyNoticeUpdateRequestDto
  ) {
    StudyNoticeResponseDto studyNoticeResponseDto = studyNoticeService.updateNotice(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        studyNoticeId,
        studyNoticeUpdateRequestDto
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study notice updated.", studyNoticeResponseDto));
  }

  @DeleteMapping("/{studyNoticeId}")
  public ResponseEntity<ApiResponseDto<Void>> deleteNotice(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long studyNoticeId
  ) {
    studyNoticeService.deleteNotice(authenticatedUserPrincipal.userId(), studyGroupId, studyNoticeId);
    return ResponseEntity.ok(ApiResponseDto.success("Study notice deleted.", null));
  }
}
