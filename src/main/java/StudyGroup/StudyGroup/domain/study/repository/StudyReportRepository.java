package StudyGroup.StudyGroup.domain.study.repository;

import StudyGroup.StudyGroup.domain.study.entity.StudyReport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyReportRepository extends JpaRepository<StudyReport, Long> {

  List<StudyReport> findByStudyGroupIdOrderByCreatedAtDesc(Long studyGroupId);

  Optional<StudyReport> findByIdAndStudyGroupId(Long id, Long studyGroupId);

  void deleteByStudyGroupId(Long studyGroupId);
}
