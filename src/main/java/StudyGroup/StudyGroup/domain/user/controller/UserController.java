package StudyGroup.StudyGroup.domain.user.controller;

import StudyGroup.StudyGroup.domain.user.dto.request.GoogleLoginRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.LocalLoginRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.LocalSignupRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.response.LocalLoginResponseDto;
import StudyGroup.StudyGroup.domain.user.dto.response.LocalSignupResponseDto;
import StudyGroup.StudyGroup.domain.user.service.UserService;
import StudyGroup.StudyGroup.global.response.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @PostMapping("/local/signup")
  public ResponseEntity<ApiResponseDto<LocalSignupResponseDto>> localSignup(
      @RequestBody @Valid LocalSignupRequestDto localSignupRequestDto
  ) {
    LocalSignupResponseDto localSignupResponseDto = userService.localSignup(localSignupRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "회원가입이 완료되었습니다.", localSignupResponseDto));
  }

  @PostMapping("/local/login")
  public ResponseEntity<ApiResponseDto<LocalLoginResponseDto>> localLogin(
      @RequestBody @Valid LocalLoginRequestDto localLoginRequestDto
  ) {
    LocalLoginResponseDto localLoginResponseDto = userService.localLogin(localLoginRequestDto);
    return ResponseEntity.ok(ApiResponseDto.success("로그인에 성공했습니다.", localLoginResponseDto));
  }

  @PostMapping("/google/login")
  public ResponseEntity<ApiResponseDto<LocalLoginResponseDto>> googleLogin(
      @RequestBody @Valid GoogleLoginRequestDto googleLoginRequestDto
  ) {
    LocalLoginResponseDto localLoginResponseDto = userService.googleLogin(googleLoginRequestDto);
    return ResponseEntity.ok(ApiResponseDto.success("구글 로그인에 성공했습니다.", localLoginResponseDto));
  }
}
