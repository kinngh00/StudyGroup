package StudyGroup.StudyGroup.domain.user.service;

import StudyGroup.StudyGroup.domain.user.dto.request.GoogleLoginRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.LocalLoginRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.LocalSignupRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.LogoutRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.TokenReissueRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.response.LocalLoginResponseDto;
import StudyGroup.StudyGroup.domain.user.dto.response.LocalSignupResponseDto;
import StudyGroup.StudyGroup.domain.user.dto.response.UserMeResponseDto;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.entity.UserProvider;
import StudyGroup.StudyGroup.domain.user.entity.UserRefreshToken;
import StudyGroup.StudyGroup.domain.user.entity.UserRole;
import StudyGroup.StudyGroup.domain.user.exception.DuplicateEmailException;
import StudyGroup.StudyGroup.domain.user.exception.InvalidCredentialsException;
import StudyGroup.StudyGroup.domain.user.exception.InvalidGoogleIdTokenException;
import StudyGroup.StudyGroup.domain.user.exception.InvalidRefreshTokenException;
import StudyGroup.StudyGroup.domain.user.exception.UserNotFoundException;
import StudyGroup.StudyGroup.domain.user.repository.UserRefreshTokenRepository;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
import StudyGroup.StudyGroup.global.auth.JwtTokenProvider;
import StudyGroup.StudyGroup.global.auth.entity.AccessTokenBlacklist;
import StudyGroup.StudyGroup.global.auth.repository.AccessTokenBlacklistRepository;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";
  private static final String GOOGLE_ISSUER_1 = "accounts.google.com";
  private static final String GOOGLE_ISSUER_2 = "https://accounts.google.com";

  private final UserRepository userRepository;
  private final UserRefreshTokenRepository userRefreshTokenRepository;
  private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Value("${google.client-id}")
  private String googleClientId;

  @Transactional
  public LocalSignupResponseDto localSignup(LocalSignupRequestDto localSignupRequestDto) {
    String normalizedEmail = localSignupRequestDto.normalizedEmail();
    String normalizedName = localSignupRequestDto.normalizedName();

    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new DuplicateEmailException();
    }

    User user = User.builder()
        .email(normalizedEmail)
        .password(passwordEncoder.encode(localSignupRequestDto.password()))
        .name(normalizedName)
        .role(UserRole.USER)
        .provider(UserProvider.LOCAL)
        .build();

    try {
      User createdUser = userRepository.save(user);
      return new LocalSignupResponseDto(createdUser);
    } catch (DataIntegrityViolationException exception) {
      throw new DuplicateEmailException();
    }
  }

  @Transactional
  public LocalLoginResponseDto localLogin(LocalLoginRequestDto localLoginRequestDto) {
    String normalizedEmail = localLoginRequestDto.normalizedEmail();

    User user = userRepository.findByEmail(normalizedEmail)
        .orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(localLoginRequestDto.password(), user.getPassword())) {
      throw new InvalidCredentialsException();
    }

    return issueAndSaveTokens(user);
  }

  @Transactional
  public LocalLoginResponseDto googleLogin(GoogleLoginRequestDto googleLoginRequestDto) {
    Map<String, Object> tokenInfo = requestGoogleTokenInfo(googleLoginRequestDto.idToken());
    validateGoogleTokenInfo(tokenInfo);

    String email = readRequiredString(tokenInfo, "email");
    String name = readOptionalString(tokenInfo, "name");
    if (name == null || name.isBlank()) {
      name = email.split("@")[0];
    }

    final String displayName = name;
    User user = userRepository.findByEmail(email)
        .orElseGet(() -> createGoogleUser(email, displayName));

    return issueAndSaveTokens(user);
  }

  @Transactional
  public LocalLoginResponseDto reissue(TokenReissueRequestDto tokenReissueRequestDto) {
    String refreshToken = tokenReissueRequestDto.refreshToken();

    if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
      throw new InvalidRefreshTokenException();
    }

    UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByRefreshToken(refreshToken)
        .orElseThrow(InvalidRefreshTokenException::new);

    if (userRefreshToken.getExpiredAt().isBefore(LocalDateTime.now())) {
      throw new InvalidRefreshTokenException();
    }

    Long tokenUserId = jwtTokenProvider.getUserId(refreshToken);
    if (!userRefreshToken.getUser().getId().equals(tokenUserId)) {
      throw new InvalidRefreshTokenException();
    }

    User user = userRefreshToken.getUser();
    return issueAndSaveTokens(user);
  }

  @Transactional
  public void logout(Long requestUserId, String accessToken, LogoutRequestDto logoutRequestDto) {
    if (accessToken == null || !jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidCredentialsException();
    }

    Long accessTokenUserId = jwtTokenProvider.getUserId(accessToken);
    if (!accessTokenUserId.equals(requestUserId)) {
      throw new InvalidCredentialsException();
    }

    LocalDateTime accessTokenExpiredAt = jwtTokenProvider.getExpiredAt(accessToken);
    accessTokenBlacklistRepository.save(AccessTokenBlacklist.builder()
        .accessToken(accessToken)
        .expiredAt(accessTokenExpiredAt)
        .build());

    UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserId(requestUserId)
        .orElseThrow(InvalidRefreshTokenException::new);

    if (!userRefreshToken.getRefreshToken().equals(logoutRequestDto.refreshToken())) {
      throw new InvalidRefreshTokenException();
    }

    userRefreshTokenRepository.delete(userRefreshToken);
    accessTokenBlacklistRepository.deleteByExpiredAtBefore(LocalDateTime.now());
  }

  public UserMeResponseDto getMe(Long requestUserId) {
    User user = userRepository.findById(requestUserId)
        .orElseThrow(UserNotFoundException::new);

    return UserMeResponseDto.from(user);
  }

  private LocalLoginResponseDto issueAndSaveTokens(User user) {
    String accessToken = jwtTokenProvider.createAccessToken(
        user.getId(),
        user.getEmail(),
        user.getRole().name()
    );

    String refreshToken = jwtTokenProvider.createRefreshToken(
        user.getId(),
        user.getEmail(),
        user.getRole().name()
    );

    saveOrUpdateRefreshToken(user, refreshToken);
    return new LocalLoginResponseDto(user, accessToken, refreshToken);
  }

  private void saveOrUpdateRefreshToken(User user, String refreshToken) {
    LocalDateTime refreshTokenExpiredAt = jwtTokenProvider.getExpiredAt(refreshToken);

    UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserId(user.getId())
        .orElse(null);

    if (userRefreshToken == null) {
      userRefreshTokenRepository.save(UserRefreshToken.builder()
          .user(user)
          .refreshToken(refreshToken)
          .expiredAt(refreshTokenExpiredAt)
          .build());
      return;
    }

    userRefreshToken.update(refreshToken, refreshTokenExpiredAt);
  }

  private User createGoogleUser(String email, String name) {
    User user = User.builder()
        .email(email)
        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
        .name(name.trim())
        .role(UserRole.USER)
        .provider(UserProvider.GOOGLE)
        .build();

    try {
      return userRepository.save(user);
    } catch (DataIntegrityViolationException exception) {
      return userRepository.findByEmail(email)
          .orElseThrow(InvalidGoogleIdTokenException::new);
    }
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
