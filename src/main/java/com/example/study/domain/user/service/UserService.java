package com.example.study.domain.user.service;

import com.example.study.domain.user.dto.UserRequestDto;
import com.example.study.domain.user.dto.UserResponseDto;
import com.example.study.domain.user.entity.User;
import com.example.study.domain.user.exception.DuplicateEmailException;
import com.example.study.domain.user.exception.UserNotFoundException;
import com.example.study.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserResponseDto createUser(UserRequestDto userRequestDto) {
    if (userRepository.existsByEmail(userRequestDto.email())) {
      throw new DuplicateEmailException();
    }

    String encodedPassword = passwordEncoder.encode(userRequestDto.password());
    User user = User.create(userRequestDto.email(), encodedPassword, userRequestDto.name());
    User savedUser = userRepository.save(user);

    return new UserResponseDto(savedUser);
  }

  public UserResponseDto getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(UserNotFoundException::new);

    return new UserResponseDto(user);
  }

  @Transactional
  public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
    User user = userRepository.findById(id)
        .orElseThrow(UserNotFoundException::new);

    String encodedPassword = passwordEncoder.encode(userRequestDto.password());
    user.updateProfile(userRequestDto.email(), encodedPassword, userRequestDto.name());

    return new UserResponseDto(user);
  }

  @Transactional
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException();
    }

    userRepository.deleteById(id);
  }
}
