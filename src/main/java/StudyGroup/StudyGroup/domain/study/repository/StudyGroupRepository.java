package StudyGroup.StudyGroup.domain.study.repository;

import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;
import StudyGroup.StudyGroup.domain.study.entity.StudyStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

  List<StudyGroup> findAllByOrderByCreatedAtDesc();

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<StudyGroup> findWithLockById(Long id);

  @Query("""
      SELECT studyGroup
      FROM StudyGroup studyGroup
      WHERE (:nameKeyword IS NULL OR LOWER(studyGroup.name) LIKE LOWER(CONCAT('%', :nameKeyword, '%')))
        AND (:status IS NULL OR studyGroup.status = :status)
      ORDER BY studyGroup.createdAt DESC
      """)
  List<StudyGroup> search(@Param("nameKeyword") String nameKeyword, @Param("status") StudyStatus status);
}
