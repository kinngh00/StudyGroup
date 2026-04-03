package StudyGroup.StudyGroup.domain.user.service;

import StudyGroup.StudyGroup.domain.user.dto.request.GoogleLoginRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.LocalLoginRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.LocalSignupRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.response.LocalLoginResponseDto;
import StudyGroup.StudyGroup.domain.user.dto.response.LocalSignupResponseDto;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.entity.UserProvider;
import StudyGroup.StudyGroup.domain.user.entity.UserRole;
import StudyGroup.StudyGroup.domain.user.exception.DuplicateEmailException;
import StudyGroup.StudyGroup.domain.user.exception.InvalidCredentialsException;
import StudyGroup.StudyGroup.domain.user.exception.InvalidGoogleIdTokenException;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
import StudyGroup.StudyGroup.global.auth.JwtTokenProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Value;
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
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Value("${google.client-id}")
  private String googleClientId;

  @Transactional
  public LocalSignupResponseDto localSignup(LocalSignupRequestDto localSignupRequestDto) {
    if (userRepository.existsByEmail(localSignupRequestDto.email())) {
      throw new DuplicateEmailException();
    }

    User user = User.builder()
        .email(localSignupRequestDto.email())
        .password(passwordEncoder.encode(localSignupRequestDto.password()))
        .name(localSignupRequestDto.name())
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

  public LocalLoginResponseDto localLogin(LocalLoginRequestDto localLoginRequestDto) {
    User user = userRepository.findByEmail(localLoginRequestDto.email())
        .orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(localLoginRequestDto.password(), user.getPassword())) {
      throw new InvalidCredentialsException();
    }

    String accessToken = jwtTokenProvider.createToken(
        user.getId(),
        user.getEmail(),
        user.getRole().name()
    );

    return new LocalLoginResponseDto(user, accessToken);
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

    String accessToken = jwtTokenProvider.createToken(
        user.getId(),
        user.getEmail(),
        user.getRole().name()
    );

    return new LocalLoginResponseDto(user, accessToken);
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
