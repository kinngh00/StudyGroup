package StudyGroup.StudyGroup.domain.study.repository;

import StudyGroup.StudyGroup.domain.study.entity.RecruitmentPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPost, Long> {

  List<RecruitmentPost> findByStudyGroupIdOrderByCreatedAtDesc(Long studyGroupId);

  Optional<RecruitmentPost> findByIdAndStudyGroupId(Long recruitmentPostId, Long studyGroupId);
}
