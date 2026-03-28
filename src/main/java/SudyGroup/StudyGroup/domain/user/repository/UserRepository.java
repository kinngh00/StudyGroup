package SudyGroup.StudyGroup.domain.user.repository;

import SudyGroup.StudyGroup.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
}
