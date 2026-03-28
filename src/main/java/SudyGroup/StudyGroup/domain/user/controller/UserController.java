package SudyGroup.StudyGroup.domain.user.controller;


import SudyGroup.StudyGroup.domain.user.dto.request.LocalSignupRequestDto;
import SudyGroup.StudyGroup.domain.user.dto.response.LocalSignupResponseDto;
import SudyGroup.StudyGroup.domain.user.service.UserService;
import SudyGroup.StudyGroup.global.exception.ExceptionResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @PostMapping("/signup")
  public LocalSignupResponseDto localSignup(@RequestBody @Valid LocalSignupRequestDto localSignupRequestDto) {
    return userService.localSignup(localSignupRequestDto);
  }
}
