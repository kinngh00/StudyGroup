package StudyGroup.StudyGroup.domain.study.repository;

import StudyGroup.StudyGroup.domain.study.entity.StudyApplication;
import StudyGroup.StudyGroup.domain.study.entity.ApplicationStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyApplicationRepository extends JpaRepository<StudyApplication, Long> {

  Optional<StudyApplication> findByIdAndRecruitmentPostIdAndStudyGroupId(
      Long applicationId,
      Long recruitmentPostId,
      Long studyGroupId
  );

  List<StudyApplication> findByRecruitmentPostIdOrderByCreatedAtDesc(Long recruitmentPostId);

  boolean existsByStudyGroupIdAndApplicantUserIdAndStatusIn(
      Long studyGroupId,
      Long applicantUserId,
      Collection<ApplicationStatus> statuses
  );

  Optional<StudyApplication> findFirstByStudyGroupIdAndApplicantUserIdAndStatusOrderByCreatedAtDesc(
      Long studyGroupId,
      Long applicantUserId,
      ApplicationStatus status
  );

  void deleteByRecruitmentPostId(Long recruitmentPostId);
}
