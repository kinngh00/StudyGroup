package StudyGroup.StudyGroup.domain.study.repository;

import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
}
