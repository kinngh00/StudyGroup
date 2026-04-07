package StudyGroup.StudyGroup.domain.study.repository;

import StudyGroup.StudyGroup.domain.study.entity.StudyMember;
import StudyGroup.StudyGroup.domain.study.entity.StudyMemberRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

  Optional<StudyMember> findByStudyGroupIdAndUserId(Long studyGroupId, Long userId);

  Optional<StudyMember> findByStudyGroupIdAndRole(Long studyGroupId, StudyMemberRole role);

  List<StudyMember> findByStudyGroupIdOrderByJoinedAtAsc(Long studyGroupId);

  long countByStudyGroupId(Long studyGroupId);

  void deleteByStudyGroupId(Long studyGroupId);

  void deleteByStudyGroupIdAndUserId(Long studyGroupId, Long userId);
}
