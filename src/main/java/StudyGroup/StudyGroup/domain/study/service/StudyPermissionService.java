package StudyGroup.StudyGroup.domain.study.service;

import StudyGroup.StudyGroup.domain.study.entity.StudyMember;
import StudyGroup.StudyGroup.domain.study.entity.StudyMemberRole;
import StudyGroup.StudyGroup.domain.study.exception.StudyAccessDeniedException;
import StudyGroup.StudyGroup.domain.study.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyPermissionService {

  private final StudyMemberRepository studyMemberRepository;

  public void validateOwnerPermission(Long studyGroupId, Long requestUserId) {
    StudyMember requester = getRequester(studyGroupId, requestUserId);

    if (requester.getRole() != StudyMemberRole.OWNER) {
      throw new StudyAccessDeniedException();
    }
  }

  public void validateManagerPermission(Long studyGroupId, Long requestUserId) {
    StudyMember requester = getRequester(studyGroupId, requestUserId);

    StudyMemberRole role = requester.getRole();
    if (role != StudyMemberRole.OWNER && role != StudyMemberRole.ADMIN) {
      throw new StudyAccessDeniedException();
    }
  }

  public void validatePostManagePermission(Long studyGroupId, Long requestUserId, Long authorUserId) {
    StudyMember requester = getRequester(studyGroupId, requestUserId);
    boolean isManager = requester.getRole() == StudyMemberRole.OWNER || requester.getRole() == StudyMemberRole.ADMIN;
    boolean isAuthor = authorUserId.equals(requestUserId);

    if (!isManager && !isAuthor) {
      throw new StudyAccessDeniedException();
    }
  }

  private StudyMember getRequester(Long studyGroupId, Long requestUserId) {
    return studyMemberRepository.findByStudyGroupIdAndUserId(studyGroupId, requestUserId)
        .orElseThrow(StudyAccessDeniedException::new);
  }
}
