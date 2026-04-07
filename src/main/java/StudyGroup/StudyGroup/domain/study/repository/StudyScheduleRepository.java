package StudyGroup.StudyGroup.domain.study.repository;

import StudyGroup.StudyGroup.domain.study.entity.StudySchedule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyScheduleRepository extends JpaRepository<StudySchedule, Long> {

  List<StudySchedule> findByStudyGroupIdOrderByScheduledAtAsc(Long studyGroupId);

  Optional<StudySchedule> findByIdAndStudyGroupId(Long studyScheduleId, Long studyGroupId);

  void deleteByStudyGroupId(Long studyGroupId);
}
