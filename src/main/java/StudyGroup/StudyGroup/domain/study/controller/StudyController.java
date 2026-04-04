package StudyGroup.StudyGroup.domain.study.controller;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyAdminAssignRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyCreateResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyDetailResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudySummaryResponseDto;
import StudyGroup.StudyGroup.domain.study.service.StudyService;
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
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyController {

  private final StudyService studyService;

  @PostMapping
  public ResponseEntity<ApiResponseDto<StudyCreateResponseDto>> createStudy(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @RequestBody @Valid StudyCreateRequestDto studyCreateRequestDto
  ) {
    StudyCreateResponseDto studyCreateResponseDto = studyService.createStudy(
        authenticatedUserPrincipal.userId(),
        studyCreateRequestDto
    );

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "스터디 그룹 생성이 완료되었습니다.", studyCreateResponseDto));
  }

  @GetMapping("/{studyGroupId}")
  public ResponseEntity<ApiResponseDto<StudyDetailResponseDto>> getStudyDetail(@PathVariable Long studyGroupId) {
    StudyDetailResponseDto studyDetailResponseDto = studyService.getStudyDetail(studyGroupId);
    return ResponseEntity.ok(ApiResponseDto.success("스터디 그룹 조회에 성공했습니다.", studyDetailResponseDto));
  }

  @GetMapping
  public ResponseEntity<ApiResponseDto<List<StudySummaryResponseDto>>> getStudies() {
    List<StudySummaryResponseDto> studySummaryResponseDtoList = studyService.getStudies();
    return ResponseEntity.ok(ApiResponseDto.success("스터디 그룹 목록 조회에 성공했습니다.", studySummaryResponseDtoList));
  }

  @PatchMapping("/{studyGroupId}")
  public ResponseEntity<ApiResponseDto<StudyDetailResponseDto>> updateStudy(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @RequestBody @Valid StudyUpdateRequestDto studyUpdateRequestDto
  ) {
    StudyDetailResponseDto studyDetailResponseDto = studyService.updateStudy(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        studyUpdateRequestDto
    );

    return ResponseEntity.ok(ApiResponseDto.success("스터디 그룹 수정이 완료되었습니다.", studyDetailResponseDto));
  }

  @DeleteMapping("/{studyGroupId}")
  public ResponseEntity<ApiResponseDto<Void>> deleteStudy(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId
  ) {
    studyService.deleteStudy(authenticatedUserPrincipal.userId(), studyGroupId);
    return ResponseEntity.ok(ApiResponseDto.success("스터디 그룹 삭제가 완료되었습니다.", null));
  }

  @PostMapping("/{studyGroupId}/admins")
  public ResponseEntity<ApiResponseDto<Void>> assignAdmin(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @RequestBody @Valid StudyAdminAssignRequestDto studyAdminAssignRequestDto
  ) {
    studyService.assignAdmin(authenticatedUserPrincipal.userId(), studyGroupId, studyAdminAssignRequestDto);
    return ResponseEntity.ok(ApiResponseDto.success("관리자 권한 부여가 완료되었습니다.", null));
  }

  @DeleteMapping("/{studyGroupId}/admins/{targetUserId}")
  public ResponseEntity<ApiResponseDto<Void>> revokeAdmin(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long targetUserId
  ) {
    studyService.revokeAdmin(authenticatedUserPrincipal.userId(), studyGroupId, targetUserId);
    return ResponseEntity.ok(ApiResponseDto.success("관리자 권한 해제가 완료되었습니다.", null));
  }
}
