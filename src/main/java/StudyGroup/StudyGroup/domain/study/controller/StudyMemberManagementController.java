package StudyGroup.StudyGroup.domain.study.controller;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyMemberKickRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyRestrictionRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyRestrictionResponseDto;
import StudyGroup.StudyGroup.domain.study.service.StudyMemberManagementService;
import StudyGroup.StudyGroup.global.auth.AuthenticatedUserPrincipal;
import StudyGroup.StudyGroup.global.response.ApiResponseDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/studies/{studyGroupId}")
public class StudyMemberManagementController {

  private final StudyMemberManagementService studyMemberManagementService;

  @PatchMapping("/members/{targetUserId}/kick")
  public ResponseEntity<ApiResponseDto<Void>> kickMember(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long targetUserId,
      @RequestBody @Valid StudyMemberKickRequestDto studyMemberKickRequestDto
  ) {
    studyMemberManagementService.kickMember(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        targetUserId,
        studyMemberKickRequestDto
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study member kicked.", null));
  }

  @PostMapping("/members/{targetUserId}/block")
  public ResponseEntity<ApiResponseDto<Void>> blockMember(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long targetUserId,
      @RequestBody @Valid StudyRestrictionRequestDto studyRestrictionRequestDto
  ) {
    studyMemberManagementService.blockMember(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        targetUserId,
        studyRestrictionRequestDto
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study member blocked.", null));
  }

  @DeleteMapping("/members/{targetUserId}/block")
  public ResponseEntity<ApiResponseDto<Void>> unblockMember(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long targetUserId
  ) {
    studyMemberManagementService.unblockMember(authenticatedUserPrincipal.userId(), studyGroupId, targetUserId);
    return ResponseEntity.ok(ApiResponseDto.success("Study member unblocked.", null));
  }

  @PostMapping("/blacklist/{targetUserId}")
  public ResponseEntity<ApiResponseDto<Void>> addBlacklist(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long targetUserId,
      @RequestBody @Valid StudyRestrictionRequestDto studyRestrictionRequestDto
  ) {
    studyMemberManagementService.addBlacklist(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        targetUserId,
        studyRestrictionRequestDto
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study member added to blacklist.", null));
  }

  @DeleteMapping("/blacklist/{targetUserId}")
  public ResponseEntity<ApiResponseDto<Void>> removeBlacklist(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long targetUserId
  ) {
    studyMemberManagementService.removeBlacklist(authenticatedUserPrincipal.userId(), studyGroupId, targetUserId);
    return ResponseEntity.ok(ApiResponseDto.success("Study member removed from blacklist.", null));
  }

  @GetMapping("/blacklist")
  public ResponseEntity<ApiResponseDto<List<StudyRestrictionResponseDto>>> getBlacklist(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId
  ) {
    List<StudyRestrictionResponseDto> studyRestrictionResponseDtoList = studyMemberManagementService.getBlacklist(
        authenticatedUserPrincipal.userId(),
        studyGroupId
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study blacklist retrieved.", studyRestrictionResponseDtoList));
  }
}
