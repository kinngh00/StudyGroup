package StudyGroup.StudyGroup.domain.user.repository;

import StudyGroup.StudyGroup.domain.user.entity.UserRefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {

  Optional<UserRefreshToken> findByUserId(Long userId);

  Optional<UserRefreshToken> findByRefreshToken(String refreshToken);

  void deleteByUserId(Long userId);
}

