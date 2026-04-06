package StudyGroup.StudyGroup.domain.study.service;

import StudyGroup.StudyGroup.domain.study.entity.StudyMember;
import StudyGroup.StudyGroup.domain.study.entity.StudyMemberRole;
import StudyGroup.StudyGroup.domain.study.entity.StudyAdminPermission;
import StudyGroup.StudyGroup.domain.study.exception.StudyAccessDeniedException;
import StudyGroup.StudyGroup.domain.study.exception.StudyUserBlockedException;
import StudyGroup.StudyGroup.domain.study.repository.StudyAdminPermissionRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyMemberRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyRestrictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyPermissionService {

  private final StudyMemberRepository studyMemberRepository;
  private final StudyAdminPermissionRepository studyAdminPermissionRepository;
  private final StudyRestrictionRepository studyRestrictionRepository;

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
    boolean isManager = hasRecruitmentWritePermission(requester);
    boolean isAuthor = authorUserId.equals(requestUserId);

    if (!isManager && !isAuthor) {
      throw new StudyAccessDeniedException();
    }
  }

  public void validateStudyMemberPermission(Long studyGroupId, Long requestUserId) {
    getRequester(studyGroupId, requestUserId);
  }

  public void validateRecruitmentWritePermission(Long studyGroupId, Long requestUserId) {
    StudyMember requester = getRequester(studyGroupId, requestUserId);
    if (!hasRecruitmentWritePermission(requester)) {
      throw new StudyAccessDeniedException();
    }
  }

  public void validateRecruitmentApprovePermission(Long studyGroupId, Long requestUserId) {
    StudyMember requester = getRequester(studyGroupId, requestUserId);
    if (!hasRecruitmentApprovePermission(requester)) {
      throw new StudyAccessDeniedException();
    }
  }

  public void validateMemberManagePermission(Long studyGroupId, Long requestUserId) {
    StudyMember requester = getRequester(studyGroupId, requestUserId);
    if (!hasMemberManagePermission(requester)) {
      throw new StudyAccessDeniedException();
    }
  }

  public void validateScheduleManagePermission(Long studyGroupId, Long requestUserId) {
    StudyMember requester = getRequester(studyGroupId, requestUserId);
    if (!hasScheduleManagePermission(requester)) {
      throw new StudyAccessDeniedException();
    }
  }

  public void validateNoticeManagePermission(Long studyGroupId, Long requestUserId) {
    StudyMember requester = getRequester(studyGroupId, requestUserId);
    if (!hasNoticeManagePermission(requester)) {
      throw new StudyAccessDeniedException();
    }
  }

  private StudyMember getRequester(Long studyGroupId, Long requestUserId) {
    validateNotBlocked(studyGroupId, requestUserId);
    return studyMemberRepository.findByStudyGroupIdAndUserId(studyGroupId, requestUserId)
        .orElseThrow(StudyAccessDeniedException::new);
  }

  private void validateNotBlocked(Long studyGroupId, Long requestUserId) {
    if (studyRestrictionRepository.existsByStudyGroupIdAndUserIdAndBlockedTrue(studyGroupId, requestUserId)) {
      throw new StudyUserBlockedException();
    }
  }

  private boolean hasRecruitmentWritePermission(StudyMember requester) {
    if (requester.getRole() == StudyMemberRole.OWNER) {
      return true;
    }
    if (requester.getRole() != StudyMemberRole.ADMIN) {
      return false;
    }
    StudyAdminPermission studyAdminPermission = getAdminPermission(requester);
    return studyAdminPermission != null && studyAdminPermission.isRecruitmentWrite();
  }

  private boolean hasRecruitmentApprovePermission(StudyMember requester) {
    if (requester.getRole() == StudyMemberRole.OWNER) {
      return true;
    }
    if (requester.getRole() != StudyMemberRole.ADMIN) {
      return false;
    }
    StudyAdminPermission studyAdminPermission = getAdminPermission(requester);
    return studyAdminPermission != null && studyAdminPermission.isRecruitmentApprove();
  }

  private boolean hasMemberManagePermission(StudyMember requester) {
    if (requester.getRole() == StudyMemberRole.OWNER) {
      return true;
    }
    if (requester.getRole() != StudyMemberRole.ADMIN) {
      return false;
    }
    StudyAdminPermission studyAdminPermission = getAdminPermission(requester);
    return studyAdminPermission != null && studyAdminPermission.isMemberManage();
  }

  private boolean hasScheduleManagePermission(StudyMember requester) {
    if (requester.getRole() == StudyMemberRole.OWNER) {
      return true;
    }
    if (requester.getRole() != StudyMemberRole.ADMIN) {
      return false;
    }
    StudyAdminPermission studyAdminPermission = getAdminPermission(requester);
    return studyAdminPermission != null && studyAdminPermission.isScheduleManage();
  }

  private boolean hasNoticeManagePermission(StudyMember requester) {
    if (requester.getRole() == StudyMemberRole.OWNER) {
      return true;
    }
    if (requester.getRole() != StudyMemberRole.ADMIN) {
      return false;
    }
    StudyAdminPermission studyAdminPermission = getAdminPermission(requester);
    return studyAdminPermission != null && studyAdminPermission.isNoticeManage();
  }

  private StudyAdminPermission getAdminPermission(StudyMember requester) {
    return studyAdminPermissionRepository.findByStudyGroupIdAndUserId(
        requester.getStudyGroup().getId(),
        requester.getUser().getId()
    ).orElse(null);
  }
}
