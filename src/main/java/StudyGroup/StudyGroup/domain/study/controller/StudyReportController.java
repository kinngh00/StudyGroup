package StudyGroup.StudyGroup.domain.study.controller;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyReportCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyReportResponseDto;
import StudyGroup.StudyGroup.domain.study.service.StudyReportService;
import StudyGroup.StudyGroup.global.auth.AuthenticatedUserPrincipal;
import StudyGroup.StudyGroup.global.response.ApiResponseDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyGroupId}/reports")
public class StudyReportController {

  private final StudyReportService studyReportService;

  @PostMapping
  public ResponseEntity<ApiResponseDto<StudyReportResponseDto>> createReport(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @RequestBody @Valid StudyReportCreateRequestDto studyReportCreateRequestDto
  ) {
    StudyReportResponseDto studyReportResponseDto = studyReportService.createReport(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        studyReportCreateRequestDto
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "Study report created.", studyReportResponseDto));
  }

  @GetMapping
  public ResponseEntity<ApiResponseDto<List<StudyReportResponseDto>>> getReports(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId
  ) {
    List<StudyReportResponseDto> studyReportResponseDtoList = studyReportService.getReports(
        authenticatedUserPrincipal.userId(),
        studyGroupId
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study report list retrieved.", studyReportResponseDtoList));
  }

  @PatchMapping("/{studyReportId}/resolve")
  public ResponseEntity<ApiResponseDto<StudyReportResponseDto>> resolveReport(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @PathVariable Long studyGroupId,
      @PathVariable Long studyReportId
  ) {
    StudyReportResponseDto studyReportResponseDto = studyReportService.resolveReport(
        authenticatedUserPrincipal.userId(),
        studyGroupId,
        studyReportId
    );
    return ResponseEntity.ok(ApiResponseDto.success("Study report resolved.", studyReportResponseDto));
  }
}
