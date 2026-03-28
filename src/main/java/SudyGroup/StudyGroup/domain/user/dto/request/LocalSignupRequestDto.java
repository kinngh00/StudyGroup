package SudyGroup.StudyGroup.domain.user.dto.request;

import SudyGroup.StudyGroup.domain.user.entity.User;

public record LocalSignupRequestDto(
    String email,
    String password,
    String name
) { }
