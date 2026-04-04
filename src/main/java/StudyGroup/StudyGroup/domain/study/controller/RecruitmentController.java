package StudyGroup.StudyGroup.domain.study.controller;

import StudyGroup.StudyGroup.domain.study.dto.request.RecruitmentPostCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.RecruitmentPostUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyApplicationCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.RecruitmentPostResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyApplicationResponseDto;
import StudyGroup.StudyGroup.domain.study.service.RecruitmentService;
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
@RequestMapping("/api/studies/{studyGroupId}/recruitment-posts")
@RequiredArgsConstructor
public class RecruitmentController {

  private final RecruitmentService recruitmentService;

  @PostMapping
  public ResponseEntity<ApiResponseDto<RecruitmentPostResponseDto>> createRecruitmentPost(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @RequestBody @Valid RecruitmentPostCreateRequestDto recruitmentPostCreateRequestDto
  ) {
    RecruitmentPostResponseDto recruitmentPostResponseDto = recruitmentService.createRecruitmentPost(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        recruitmentPostCreateRequestDto
    );

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "모집글 생성이 완료되었습니다.", recruitmentPostResponseDto));
  }

  @GetMapping
  public ResponseEntity<ApiResponseDto<List<RecruitmentPostResponseDto>>> getRecruitmentPosts(
      @PathVariable Long studyGroupId
  ) {
    List<RecruitmentPostResponseDto> recruitmentPostResponseDtoList = recruitmentService.getRecruitmentPosts(studyGroupId);
    return ResponseEntity.ok(ApiResponseDto.success("모집글 목록 조회에 성공했습니다.", recruitmentPostResponseDtoList));
  }

  @GetMapping("/{recruitmentPostId}")
  public ResponseEntity<ApiResponseDto<RecruitmentPostResponseDto>> getRecruitmentPost(
      @PathVariable Long studyGroupId,
      @PathVariable Long recruitmentPostId
  ) {
    RecruitmentPostResponseDto recruitmentPostResponseDto = recruitmentService.getRecruitmentPost(studyGroupId, recruitmentPostId);
    return ResponseEntity.ok(ApiResponseDto.success("모집글 조회에 성공했습니다.", recruitmentPostResponseDto));
  }

  @PatchMapping("/{recruitmentPostId}")
  public ResponseEntity<ApiResponseDto<RecruitmentPostResponseDto>> updateRecruitmentPost(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long recruitmentPostId,
      @RequestBody @Valid RecruitmentPostUpdateRequestDto recruitmentPostUpdateRequestDto
  ) {
    RecruitmentPostResponseDto recruitmentPostResponseDto = recruitmentService.updateRecruitmentPost(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        recruitmentPostId,
        recruitmentPostUpdateRequestDto
    );

    return ResponseEntity.ok(ApiResponseDto.success("모집글 수정이 완료되었습니다.", recruitmentPostResponseDto));
  }

  @DeleteMapping("/{recruitmentPostId}")
  public ResponseEntity<ApiResponseDto<Void>> deleteRecruitmentPost(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long recruitmentPostId
  ) {
    recruitmentService.deleteRecruitmentPost(authenticatedUserPrincipal.userId(), studyGroupId, recruitmentPostId);
    return ResponseEntity.ok(ApiResponseDto.success("모집글 삭제가 완료되었습니다.", null));
  }

  @PostMapping("/{recruitmentPostId}/applications")
  public ResponseEntity<ApiResponseDto<StudyApplicationResponseDto>> applyStudy(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long recruitmentPostId,
      @RequestBody @Valid StudyApplicationCreateRequestDto studyApplicationCreateRequestDto
  ) {
    StudyApplicationResponseDto studyApplicationResponseDto = recruitmentService.applyStudy(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        recruitmentPostId,
        studyApplicationCreateRequestDto
    );

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "가입 신청이 완료되었습니다.", studyApplicationResponseDto));
  }

  @GetMapping("/{recruitmentPostId}/applications")
  public ResponseEntity<ApiResponseDto<List<StudyApplicationResponseDto>>> getApplications(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long recruitmentPostId
  ) {
    List<StudyApplicationResponseDto> studyApplicationResponseDtoList = recruitmentService.getApplications(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        recruitmentPostId
    );

    return ResponseEntity.ok(ApiResponseDto.success("가입 신청 목록 조회에 성공했습니다.", studyApplicationResponseDtoList));
  }

  @PatchMapping("/{recruitmentPostId}/applications/{applicationId}/approve")
  public ResponseEntity<ApiResponseDto<StudyApplicationResponseDto>> approveApplication(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long recruitmentPostId,
      @PathVariable Long applicationId
  ) {
    StudyApplicationResponseDto studyApplicationResponseDto = recruitmentService.approveApplication(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        recruitmentPostId,
        applicationId
    );

    return ResponseEntity.ok(ApiResponseDto.success("가입 신청 승인 처리가 완료되었습니다.", studyApplicationResponseDto));
  }

  @PatchMapping("/{recruitmentPostId}/applications/{applicationId}/reject")
  public ResponseEntity<ApiResponseDto<StudyApplicationResponseDto>> rejectApplication(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long recruitmentPostId,
      @PathVariable Long applicationId
  ) {
    StudyApplicationResponseDto studyApplicationResponseDto = recruitmentService.rejectApplication(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        recruitmentPostId,
        applicationId
    );

    return ResponseEntity.ok(ApiResponseDto.success("가입 신청 거절 처리가 완료되었습니다.", studyApplicationResponseDto));
  }
}
