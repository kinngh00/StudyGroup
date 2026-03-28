package StudyGroup.StudyGroup.domain.user.service;

import StudyGroup.StudyGroup.domain.user.dto.request.LocalLoginRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.request.LocalSignupRequestDto;
import StudyGroup.StudyGroup.domain.user.dto.response.LocalLoginResponseDto;
import StudyGroup.StudyGroup.domain.user.dto.response.LocalSignupResponseDto;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.entity.UserProvider;
import StudyGroup.StudyGroup.domain.user.entity.UserRole;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
import StudyGroup.StudyGroup.global.auth.JwtTokenProvider;
import StudyGroup.StudyGroup.global.exception.CustomException;
import StudyGroup.StudyGroup.global.exception.ErrorCode;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Transactional
  public LocalSignupResponseDto localSignup(LocalSignupRequestDto requestDto) {
    String email = normalizeEmail(requestDto.email());
    String name = normalizeName(requestDto.name());

    if (userRepository.existsByEmail(email)) {
      throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
    }

    User user = User.builder()
        .email(email)
        .password(passwordEncoder.encode(requestDto.password()))
        .name(name)
        .role(UserRole.USER)
        .provider(UserProvider.LOCAL)
        .build();

    try {
      User createdUser = userRepository.save(user);
      return new LocalSignupResponseDto(createdUser);
    } catch (DataIntegrityViolationException e) {
      throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
    }
  }

  public LocalLoginResponseDto localLogin(LocalLoginRequestDto requestDto) {
    String email = normalizeEmail(requestDto.email());

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

    if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
      throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
    }

    String accessToken = jwtTokenProvider.createToken(
        user.getId(),
        user.getEmail(),
        user.getRole().name()
    );

    return new LocalLoginResponseDto(user, accessToken);
  }

  private String normalizeEmail(String rawEmail) {
    return rawEmail.trim().toLowerCase(Locale.ROOT);
  }

  private String normalizeName(String rawName) {
    return rawName.trim();
  }
}

