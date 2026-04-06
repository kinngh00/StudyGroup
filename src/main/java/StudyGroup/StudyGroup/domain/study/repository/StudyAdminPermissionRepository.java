package StudyGroup.StudyGroup.domain.study.repository;

import StudyGroup.StudyGroup.domain.study.entity.StudyAdminPermission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyAdminPermissionRepository extends JpaRepository<StudyAdminPermission, Long> {

  Optional<StudyAdminPermission> findByStudyGroupIdAndUserId(Long studyGroupId, Long userId);

  List<StudyAdminPermission> findByStudyGroupId(Long studyGroupId);

  void deleteByStudyGroupIdAndUserId(Long studyGroupId, Long userId);

  void deleteByStudyGroupId(Long studyGroupId);
}
