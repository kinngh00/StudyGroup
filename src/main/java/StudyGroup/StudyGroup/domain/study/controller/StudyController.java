package StudyGroup.StudyGroup.domain.study.controller;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyCreateResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyDetailResponseDto;
import StudyGroup.StudyGroup.domain.study.service.StudyService;
import StudyGroup.StudyGroup.global.response.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
      Authentication authentication,
      @RequestBody @Valid StudyCreateRequestDto studyCreateRequestDto
  ) {
    Long requestUserId = studyService.extractUserId(authentication.getPrincipal());
    StudyCreateResponseDto response = studyService.createStudy(requestUserId, studyCreateRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "스터디 그룹 생성이 완료되었습니다.", response));
  }

  @GetMapping("/{studyGroupId}")
  public ResponseEntity<ApiResponseDto<StudyDetailResponseDto>> getStudyDetail(@PathVariable Long studyGroupId) {
    StudyDetailResponseDto response = studyService.getStudyDetail(studyGroupId);
    return ResponseEntity.ok(ApiResponseDto.success("스터디 그룹 조회에 성공했습니다.", response));
  }
}
