package StudyGroup.StudyGroup.domain.user.controller;

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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<ApiResponseDto<LocalSignupResponseDto>> localSignup(
      @RequestBody @Valid LocalSignupRequestDto requestDto
  ) {
    LocalSignupResponseDto response = userService.localSignup(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(
            HttpStatus.CREATED.value(),
            "회원가입이 완료되었습니다.",
            response
        ));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponseDto<LocalLoginResponseDto>> localLogin(
      @RequestBody @Valid LocalLoginRequestDto requestDto
  ) {
    LocalLoginResponseDto response = userService.localLogin(requestDto);
    return ResponseEntity.ok(ApiResponseDto.success(
        "로그인에 성공했습니다.",
        response
    ));
  }
}
