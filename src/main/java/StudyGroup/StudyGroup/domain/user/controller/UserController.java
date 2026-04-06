package StudyGroup.StudyGroup.domain.user.controller;

import StudyGroup.StudyGroup.domain.user.dto.request.GoogleLoginRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.LocalLoginRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.LocalSignupRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.LogoutRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.TokenReissueRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.response.LocalLoginResponseDto;
import StudyGroup.StudyGroup.domain.user.dto.response.LocalSignupResponseDto;
import StudyGroup.StudyGroup.domain.user.dto.response.UserMeResponseDto;
import StudyGroup.StudyGroup.domain.user.exception.InvalidCredentialsException;
import StudyGroup.StudyGroup.domain.user.service.UserService;
import StudyGroup.StudyGroup.global.auth.AuthenticatedUserPrincipal;
import StudyGroup.StudyGroup.global.response.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
        .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "ȸ�������� �Ϸ�Ǿ����ϴ�.", localSignupResponseDto));
  }

  @PostMapping("/local/login")
  public ResponseEntity<ApiResponseDto<LocalLoginResponseDto>> localLogin(
      @RequestBody @Valid LocalLoginRequestDto localLoginRequestDto
  ) {
    LocalLoginResponseDto localLoginResponseDto = userService.localLogin(localLoginRequestDto);
    return ResponseEntity.ok(ApiResponseDto.success("�α��ο� �����߽��ϴ�.", localLoginResponseDto));
  }

  @PostMapping("/google/login")
  public ResponseEntity<ApiResponseDto<LocalLoginResponseDto>> googleLogin(
      @RequestBody @Valid GoogleLoginRequestDto googleLoginRequestDto
  ) {
    LocalLoginResponseDto localLoginResponseDto = userService.googleLogin(googleLoginRequestDto);
    return ResponseEntity.ok(ApiResponseDto.success("���� �α��ο� �����߽��ϴ�.", localLoginResponseDto));
  }

  @PostMapping("/local/reissue")
  public ResponseEntity<ApiResponseDto<LocalLoginResponseDto>> reissue(
      @RequestBody @Valid TokenReissueRequestDto tokenReissueRequestDto
  ) {
    LocalLoginResponseDto localLoginResponseDto = userService.reissue(tokenReissueRequestDto);
    return ResponseEntity.ok(ApiResponseDto.success("��ū ��߱��� �Ϸ�Ǿ����ϴ�.", localLoginResponseDto));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponseDto<Void>> logout(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal,
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestBody @Valid LogoutRequestDto logoutRequestDto
  ) {
    String accessToken = extractBearerToken(authorizationHeader);
    userService.logout(authenticatedUserPrincipal.userId(), accessToken, logoutRequestDto);
    return ResponseEntity.ok(ApiResponseDto.success("�α׾ƿ��� �Ϸ�Ǿ����ϴ�.", null));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponseDto<UserMeResponseDto>> me(
      @AuthenticationPrincipal AuthenticatedUserPrincipal authenticatedUserPrincipal
  ) {
    UserMeResponseDto userMeResponseDto = userService.getMe(authenticatedUserPrincipal.userId());
    return ResponseEntity.ok(ApiResponseDto.success("�� ���� ��ȸ�� �����߽��ϴ�.", userMeResponseDto));
  }

  private String extractBearerToken(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new InvalidCredentialsException();
    }
    return authorizationHeader.substring(7);
  }
}
