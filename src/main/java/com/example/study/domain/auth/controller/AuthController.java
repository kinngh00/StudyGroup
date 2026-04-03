package com.example.study.domain.auth.controller;

import com.example.study.domain.auth.dto.request.LoginRequestDto;
import com.example.study.domain.auth.dto.request.GoogleLoginRequestDto;
import com.example.study.domain.auth.dto.request.SignUpRequestDto;
import com.example.study.domain.auth.dto.request.TokenReissueRequestDto;
import com.example.study.domain.auth.dto.response.AuthTokenResponseDto;
import com.example.study.domain.auth.dto.response.LoginResponseDto;
import com.example.study.domain.auth.dto.response.MeResponseDto;
import com.example.study.domain.auth.dto.response.SignUpResponseDto;
import com.example.study.domain.auth.service.AuthService;
import com.example.study.global.config.security.dto.AuthUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class AuthController {

  private static final String BEARER_PREFIX = "Bearer ";

  private final AuthService authService;

  @PostMapping("/local/signup")
  public SignUpResponseDto signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
    return authService.signUp(signUpRequestDto);
  }

  @PostMapping("/local/login")
  public LoginResponseDto login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
    return authService.login(loginRequestDto);
  }

  @PostMapping("/google/login")
  public LoginResponseDto googleLogin(@Valid @RequestBody GoogleLoginRequestDto googleLoginRequestDto) {
    return authService.googleLogin(googleLoginRequestDto);
  }

  @PostMapping("/local/reissue")
  public AuthTokenResponseDto reissue(@Valid @RequestBody TokenReissueRequestDto tokenReissueRequestDto) {
    return authService.reissue(tokenReissueRequestDto);
  }

  @PostMapping("/logout")
  public void logout(
      @AuthenticationPrincipal AuthUserPrincipal authUserPrincipal,
      @RequestHeader(value = "Authorization", required = false) String authorizationHeader
  ) {
    String accessToken = resolveAccessToken(authorizationHeader);
    authService.logout(authUserPrincipal.userId(), accessToken);
  }

  @GetMapping("/me")
  public MeResponseDto me(@AuthenticationPrincipal AuthUserPrincipal authUserPrincipal) {
    return authService.getMyInfo(authUserPrincipal.userId());
  }

  private String resolveAccessToken(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      return null;
    }

    return authorizationHeader.substring(BEARER_PREFIX.length());
  }
}
