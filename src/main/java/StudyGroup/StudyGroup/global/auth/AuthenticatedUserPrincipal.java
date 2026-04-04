package StudyGroup.StudyGroup.global.auth;

public record AuthenticatedUserPrincipal(
    Long userId,
    String email,
    String role
) {
}
