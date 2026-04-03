package com.example.study.domain.auth.service;

import com.example.study.domain.auth.dto.request.GoogleLoginRequestDto;
import com.example.study.domain.auth.dto.request.LoginRequestDto;
import com.example.study.domain.auth.dto.request.SignUpRequestDto;
import com.example.study.domain.auth.dto.request.TokenReissueRequestDto;
import com.example.study.domain.auth.dto.response.AuthTokenResponseDto;
import com.example.study.domain.auth.dto.response.LoginResponseDto;
import com.example.study.domain.auth.dto.response.MeResponseDto;
import com.example.study.domain.auth.dto.response.SignUpResponseDto;
import com.example.study.domain.auth.exception.InvalidCredentialsException;
import com.example.study.domain.auth.exception.InvalidGoogleIdTokenException;
import com.example.study.domain.auth.exception.InvalidRefreshTokenException;
import com.example.study.domain.auth.exception.RefreshTokenExpiredException;
import com.example.study.domain.auth.entity.AccessTokenBlacklist;
import com.example.study.domain.auth.entity.RefreshToken;
import com.example.study.domain.auth.repository.AccessTokenBlacklistRepository;
import com.example.study.domain.auth.repository.RefreshTokenRepository;
import com.example.study.domain.user.entity.User;
import com.example.study.domain.user.exception.DuplicateEmailException;
import com.example.study.domain.user.exception.UserNotFoundException;
import com.example.study.domain.user.repository.UserRepository;
import com.example.study.global.config.security.JwtTokenProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";
  private static final String GOOGLE_ISSUER_1 = "accounts.google.com";
  private static final String GOOGLE_ISSUER_2 = "https://accounts.google.com";

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Value("${google.client-id}")
  private String googleClientId;

  @Transactional
  public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
    if (userRepository.existsByEmail(signUpRequestDto.email())) {
      throw new DuplicateEmailException();
    }

    String encodedPassword = passwordEncoder.encode(signUpRequestDto.password());
    User user = User.create(signUpRequestDto.email(), encodedPassword, signUpRequestDto.name());
    User savedUser = userRepository.save(user);

    return SignUpResponseDto.from(savedUser);
  }

  @Transactional
  public LoginResponseDto login(LoginRequestDto loginRequestDto) {
    User user = userRepository.findByEmail(loginRequestDto.email())
        .orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
      throw new InvalidCredentialsException();
    }

    AuthTokenResponseDto authTokenResponseDto = issueTokenPair(user);
    return LoginResponseDto.of(user, authTokenResponseDto);
  }

  @Transactional
  public LoginResponseDto googleLogin(GoogleLoginRequestDto googleLoginRequestDto) {
    Map<String, Object> tokenInfo = requestGoogleTokenInfo(googleLoginRequestDto.idToken());
    validateGoogleTokenInfo(tokenInfo);

    String email = readRequiredString(tokenInfo, "email");
    String name = readOptionalString(tokenInfo, "name");
    if (name == null || name.isBlank()) {
      name = email.split("@")[0];
    }
    String displayName = name;

    User user = userRepository.findByEmail(email)
        .orElseGet(() -> createGoogleUser(email, displayName));

    AuthTokenResponseDto authTokenResponseDto = issueTokenPair(user);
    return LoginResponseDto.of(user, authTokenResponseDto);
  }

  @Transactional
  public AuthTokenResponseDto reissue(TokenReissueRequestDto tokenReissueRequestDto) {
    String refreshToken = tokenReissueRequestDto.refreshToken();

    if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
      throw new InvalidRefreshTokenException();
    }

    Long userId = jwtTokenProvider.getUserId(refreshToken);
    User user = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);

    RefreshToken storedRefreshToken = refreshTokenRepository.findById(userId)
        .orElseThrow(InvalidRefreshTokenException::new);

    if (storedRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(storedRefreshToken);
      throw new RefreshTokenExpiredException();
    }

    if (!passwordEncoder.matches(refreshToken, storedRefreshToken.getTokenHash())) {
      throw new InvalidRefreshTokenException();
    }

    return issueTokenPair(user);
  }

  @Transactional
  public void logout(Long userId, String accessToken) {
    userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);

    if (accessToken != null
        && jwtTokenProvider.validateToken(accessToken)
        && jwtTokenProvider.isAccessToken(accessToken)) {
      LocalDateTime expiration = jwtTokenProvider.getExpiration(accessToken);
      accessTokenBlacklistRepository.save(AccessTokenBlacklist.create(accessToken, expiration));
    }

    refreshTokenRepository.deleteById(userId);
    accessTokenBlacklistRepository.deleteByExpiresAtBefore(LocalDateTime.now());
  }

  public MeResponseDto getMyInfo(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);

    return MeResponseDto.from(user);
  }

  private AuthTokenResponseDto issueTokenPair(User user) {
    String accessToken = jwtTokenProvider.createAccessToken(user);
    String refreshToken = jwtTokenProvider.createRefreshToken(user);

    LocalDateTime refreshTokenExpiration = jwtTokenProvider.getExpiration(refreshToken);
    String hashedRefreshToken = passwordEncoder.encode(refreshToken);

    RefreshToken refreshTokenEntity = refreshTokenRepository.findById(user.getId())
        .map(existingRefreshToken -> {
          existingRefreshToken.update(hashedRefreshToken, refreshTokenExpiration);
          return existingRefreshToken;
        })
        .orElseGet(() -> RefreshToken.create(user, hashedRefreshToken, refreshTokenExpiration));

    refreshTokenRepository.save(refreshTokenEntity);
    accessTokenBlacklistRepository.deleteByExpiresAtBefore(LocalDateTime.now());

    return new AuthTokenResponseDto(accessToken, refreshToken);
  }

  private User createGoogleUser(String email, String name) {
    String randomPassword = UUID.randomUUID().toString();
    String encodedPassword = passwordEncoder.encode(randomPassword);
    User user = User.create(email, encodedPassword, name.trim());
    return userRepository.save(user);
  }

  private Map<String, Object> requestGoogleTokenInfo(String idToken) {
    try {
      String encodedIdToken = URLEncoder.encode(idToken, StandardCharsets.UTF_8);
      HttpRequest httpRequest = HttpRequest.newBuilder()
          .uri(URI.create(GOOGLE_TOKEN_INFO_URL + encodedIdToken))
          .GET()
          .build();

      HttpResponse<String> httpResponse = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
      if (httpResponse.statusCode() != 200) {
        throw new InvalidGoogleIdTokenException();
      }

      return OBJECT_MAPPER.readValue(httpResponse.body(), new TypeReference<Map<String, Object>>() {});
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw new InvalidGoogleIdTokenException();
    } catch (IOException exception) {
      throw new InvalidGoogleIdTokenException();
    }
  }

  private void validateGoogleTokenInfo(Map<String, Object> tokenInfo) {
    String audience = readRequiredString(tokenInfo, "aud");
    if (!googleClientId.equals(audience)) {
      throw new InvalidGoogleIdTokenException();
    }

    String issuer = readRequiredString(tokenInfo, "iss");
    if (!GOOGLE_ISSUER_1.equals(issuer) && !GOOGLE_ISSUER_2.equals(issuer)) {
      throw new InvalidGoogleIdTokenException();
    }

    String emailVerified = readRequiredString(tokenInfo, "email_verified");
    if (!"true".equalsIgnoreCase(emailVerified)) {
      throw new InvalidGoogleIdTokenException();
    }

    long expirationSeconds = Long.parseLong(readRequiredString(tokenInfo, "exp"));
    long nowSeconds = OffsetDateTime.now().toEpochSecond();
    if (expirationSeconds <= nowSeconds) {
      throw new InvalidGoogleIdTokenException();
    }
  }

  private String readRequiredString(Map<String, Object> tokenInfo, String key) {
    Object value = tokenInfo.get(key);
    if (value == null) {
      throw new InvalidGoogleIdTokenException();
    }
    String stringValue = value.toString();
    if (stringValue.isBlank()) {
      throw new InvalidGoogleIdTokenException();
    }
    return stringValue;
  }

  private String readOptionalString(Map<String, Object> tokenInfo, String key) {
    Object value = tokenInfo.get(key);
    return value == null ? null : value.toString();
  }
}
