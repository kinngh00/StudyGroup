package StudyGroup.StudyGroup.domain.study.controller;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyAdminAssignRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyAdminPermissionUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyAdminPermissionResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyCreateResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyDetailResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyMemberResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyRoleResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudySummaryResponseDto;
import StudyGroup.StudyGroup.domain.study.entity.StudyStatus;
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
import org.springframework.web.bind.annotation.RequestParam;
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
        .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "Study created.", studyCreateResponseDto));
  }

  @GetMapping("/{studyGroupId}")
  public ResponseEntity<ApiResponseDto<StudyDetailResponseDto>> getStudyDetail(@PathVariable Long studyGroupId) {
    StudyDetailResponseDto studyDetailResponseDto = studyService.getStudyDetail(studyGroupId);
    return ResponseEntity.ok(ApiResponseDto.success("Study detail retrieved.", studyDetailResponseDto));
  }

  @GetMapping
  public ResponseEntity<ApiResponseDto<List<StudySummaryResponseDto>>> getStudies(
      @RequestParam(required = false) String nameKeyword,
      @RequestParam(required = false) StudyStatus status,
      @RequestParam(required = false) Boolean isClosed
  ) {
    List<StudySummaryResponseDto> studySummaryResponseDtoList = studyService.getStudies(nameKeyword, status, isClosed);
    return ResponseEntity.ok(ApiResponseDto.success("Study list retrieved.", studySummaryResponseDtoList));
  }

  @GetMapping("/{studyGroupId}/role")
  public ResponseEntity<ApiResponseDto<StudyRoleResponseDto>> getMyRole(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId
  ) {
    StudyRoleResponseDto studyRoleResponseDto = studyService.getMyRole(authenticatedUserPrincipal.userId(), studyGroupId);
    return ResponseEntity.ok(ApiResponseDto.success("Study role retrieved.", studyRoleResponseDto));
  }

  @GetMapping("/{studyGroupId}/members")
  public ResponseEntity<ApiResponseDto<List<StudyMemberResponseDto>>> getStudyMembers(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId
  ) {
    List<StudyMemberResponseDto> studyMemberResponseDtoList = studyService.getStudyMembers(
        authenticatedUserPrincipal.userId(),
        studyGroupId
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study member list retrieved.", studyMemberResponseDtoList));
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

    return ResponseEntity.ok(ApiResponseDto.success("Study updated.", studyDetailResponseDto));
  }

  @DeleteMapping("/{studyGroupId}")
  public ResponseEntity<ApiResponseDto<Void>> deleteStudy(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId
  ) {
    studyService.deleteStudy(authenticatedUserPrincipal.userId(), studyGroupId);
    return ResponseEntity.ok(ApiResponseDto.success("Study deleted.", null));
  }

  @PostMapping("/{studyGroupId}/admins")
  public ResponseEntity<ApiResponseDto<StudyAdminPermissionResponseDto>> assignAdmin(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @RequestBody @Valid StudyAdminAssignRequestDto studyAdminAssignRequestDto
  ) {
    StudyAdminPermissionResponseDto studyAdminPermissionResponseDto = studyService.assignAdmin(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        studyAdminAssignRequestDto
    );
    return ResponseEntity.ok(ApiResponseDto.success("Admin assigned.", studyAdminPermissionResponseDto));
  }

  @PatchMapping("/{studyGroupId}/admins/{targetUserId}/permissions")
  public ResponseEntity<ApiResponseDto<StudyAdminPermissionResponseDto>> updateAdminPermission(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long targetUserId,
      @RequestBody @Valid StudyAdminPermissionUpdateRequestDto studyAdminPermissionUpdateRequestDto
  ) {
    StudyAdminPermissionResponseDto studyAdminPermissionResponseDto = studyService.updateAdminPermission(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        targetUserId,
        studyAdminPermissionUpdateRequestDto
    );
    return ResponseEntity.ok(ApiResponseDto.success("Admin permission updated.", studyAdminPermissionResponseDto));
  }

  @DeleteMapping("/{studyGroupId}/admins/{targetUserId}")
  public ResponseEntity<ApiResponseDto<Void>> revokeAdmin(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long targetUserId
  ) {
    studyService.revokeAdmin(authenticatedUserPrincipal.userId(), studyGroupId, targetUserId);
    return ResponseEntity.ok(ApiResponseDto.success("Admin revoked.", null));
  }
}
