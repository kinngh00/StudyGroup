package StudyGroup.StudyGroup.domain.study.repository;

import StudyGroup.StudyGroup.domain.study.entity.StudyNotice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyNoticeRepository extends JpaRepository<StudyNotice, Long> {

  List<StudyNotice> findByStudyGroupIdOrderByPinnedDescCreatedAtDesc(Long studyGroupId);

  Optional<StudyNotice> findByIdAndStudyGroupId(Long id, Long studyGroupId);

  void deleteByStudyGroupId(Long studyGroupId);
}
