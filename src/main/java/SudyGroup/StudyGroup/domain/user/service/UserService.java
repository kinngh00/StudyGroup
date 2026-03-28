package SudyGroup.StudyGroup.domain.user.service;

import SudyGroup.StudyGroup.domain.user.dto.request.LocalSignupRequestDto;
import SudyGroup.StudyGroup.domain.user.dto.response.LocalSignupResponseDto;
import SudyGroup.StudyGroup.domain.user.entity.User;
import SudyGroup.StudyGroup.domain.user.entity.UserProvider;
import SudyGroup.StudyGroup.domain.user.entity.UserRole;
import SudyGroup.StudyGroup.domain.user.repository.UserRepository;
import SudyGroup.StudyGroup.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public LocalSignupResponseDto localSignup(LocalSignupRequestDto localSignupRequestDto) {

    if(userRepository.existsByEmail(localSignupRequestDto.email())) {
      throw new CustomException();
    }

    String encodedPassword = passwordEncoder.encode(localSignupRequestDto.password());

    User user = User.builder()
        .email(localSignupRequestDto.email())
        .password(encodedPassword)
        .name(localSignupRequestDto.name())
        .role(UserRole.USER)
        .provider(UserProvider.LOCAL)
        .build();

    User createdLocal = userRepository.save(user);

    return new LocalSignupResponseDto(createdLocal);
  }
}
