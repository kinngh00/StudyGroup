package StudyGroup.StudyGroup.domain.study.repository;

import StudyGroup.StudyGroup.domain.study.entity.StudyRestriction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRestrictionRepository extends JpaRepository<StudyRestriction, Long> {

  Optional<StudyRestriction> findByStudyGroupIdAndUserId(Long studyGroupId, Long userId);

  boolean existsByStudyGroupIdAndUserIdAndBlacklistedTrue(Long studyGroupId, Long userId);

  boolean existsByStudyGroupIdAndUserIdAndBlockedTrue(Long studyGroupId, Long userId);

  List<StudyRestriction> findByStudyGroupIdAndBlacklistedTrueOrderByUpdatedAtDesc(Long studyGroupId);

  void deleteByStudyGroupId(Long studyGroupId);
}
