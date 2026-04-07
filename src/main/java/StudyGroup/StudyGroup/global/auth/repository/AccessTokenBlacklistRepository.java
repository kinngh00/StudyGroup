package StudyGroup.StudyGroup.global.auth.repository;

import StudyGroup.StudyGroup.global.auth.entity.AccessTokenBlacklist;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenBlacklistRepository extends JpaRepository<AccessTokenBlacklist, Long> {

  boolean existsByAccessToken(String accessToken);

  void deleteByExpiredAtBefore(LocalDateTime now);
}

