package StudyGroup.StudyGroup.domain.study.service;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyMemberKickRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyRestrictionRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyRestrictionResponseDto;
import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;
import StudyGroup.StudyGroup.domain.study.entity.StudyMember;
import StudyGroup.StudyGroup.domain.study.entity.StudyMemberRole;
import StudyGroup.StudyGroup.domain.study.entity.StudyRestriction;
import StudyGroup.StudyGroup.domain.study.exception.StudyAccessDeniedException;
import StudyGroup.StudyGroup.domain.study.exception.StudyGroupNotFoundException;
import StudyGroup.StudyGroup.domain.study.exception.StudyMemberNotFoundException;
import StudyGroup.StudyGroup.domain.study.repository.StudyGroupRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyMemberRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyRestrictionRepository;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyMemberManagementService {

  private final StudyGroupRepository studyGroupRepository;
  private final StudyMemberRepository studyMemberRepository;
  private final StudyRestrictionRepository studyRestrictionRepository;
  private final UserRepository userRepository;
  private final StudyPermissionService studyPermissionService;

  @Transactional
  public void kickMember(
      Long requestUserId,
      Long studyGroupId,
      Long targetUserId,
      StudyMemberKickRequestDto studyMemberKickRequestDto
  ) {
    getStudyGroup(studyGroupId);
    studyPermissionService.validateMemberManagePermission(studyGroupId, requestUserId);

    StudyMember targetStudyMember = studyMemberRepository.findByStudyGroupIdAndUserId(studyGroupId, targetUserId)
        .orElseThrow(StudyMemberNotFoundException::new);

    validateKickTarget(targetStudyMember, requestUserId);
    studyMemberRepository.deleteByStudyGroupIdAndUserId(studyGroupId, targetUserId);

    if (studyMemberKickRequestDto.addToBlacklist()) {
      StudyRestriction studyRestriction = getOrCreateRestriction(studyGroupId, targetUserId);
      studyRestriction.addBlacklist(studyMemberKickRequestDto.normalizedReason());
    }
  }

  @Transactional
  public void blockMember(
      Long requestUserId,
      Long studyGroupId,
      Long targetUserId,
      StudyRestrictionRequestDto studyRestrictionRequestDto
  ) {
    getStudyGroup(studyGroupId);
    studyPermissionService.validateMemberManagePermission(studyGroupId, requestUserId);

    StudyMember targetStudyMember = studyMemberRepository.findByStudyGroupIdAndUserId(studyGroupId, targetUserId)
        .orElseThrow(StudyMemberNotFoundException::new);

    if (targetStudyMember.getRole() == StudyMemberRole.OWNER || targetUserId.equals(requestUserId)) {
      throw new StudyAccessDeniedException();
    }

    StudyRestriction studyRestriction = getOrCreateRestriction(studyGroupId, targetUserId);
    studyRestriction.block(studyRestrictionRequestDto.normalizedReason());
  }

  @Transactional
  public void unblockMember(Long requestUserId, Long studyGroupId, Long targetUserId) {
    getStudyGroup(studyGroupId);
    studyPermissionService.validateMemberManagePermission(studyGroupId, requestUserId);

    StudyRestriction studyRestriction = studyRestrictionRepository.findByStudyGroupIdAndUserId(studyGroupId, targetUserId)
        .orElse(null);

    if (studyRestriction != null) {
      studyRestriction.unblock();
    }
  }

  @Transactional
  public void addBlacklist(
      Long requestUserId,
      Long studyGroupId,
      Long targetUserId,
      StudyRestrictionRequestDto studyRestrictionRequestDto
  ) {
    getStudyGroup(studyGroupId);
    studyPermissionService.validateMemberManagePermission(studyGroupId, requestUserId);

    StudyRestriction studyRestriction = getOrCreateRestriction(studyGroupId, targetUserId);
    studyRestriction.addBlacklist(studyRestrictionRequestDto.normalizedReason());
  }

  @Transactional
  public void removeBlacklist(Long requestUserId, Long studyGroupId, Long targetUserId) {
    getStudyGroup(studyGroupId);
    studyPermissionService.validateMemberManagePermission(studyGroupId, requestUserId);

    StudyRestriction studyRestriction = studyRestrictionRepository.findByStudyGroupIdAndUserId(studyGroupId, targetUserId)
        .orElse(null);

    if (studyRestriction != null) {
      studyRestriction.removeBlacklist();
    }
  }

  public List<StudyRestrictionResponseDto> getBlacklist(Long requestUserId, Long studyGroupId) {
    getStudyGroup(studyGroupId);
    studyPermissionService.validateMemberManagePermission(studyGroupId, requestUserId);

    return studyRestrictionRepository.findByStudyGroupIdAndBlacklistedTrueOrderByUpdatedAtDesc(studyGroupId).stream()
        .map(StudyRestrictionResponseDto::from)
        .toList();
  }

  private StudyGroup getStudyGroup(Long studyGroupId) {
    return studyGroupRepository.findById(studyGroupId)
        .orElseThrow(StudyGroupNotFoundException::new);
  }

  private StudyRestriction getOrCreateRestriction(Long studyGroupId, Long targetUserId) {
    StudyRestriction studyRestriction = studyRestrictionRepository.findByStudyGroupIdAndUserId(studyGroupId, targetUserId)
        .orElse(null);

    if (studyRestriction != null) {
      return studyRestriction;
    }

    StudyGroup studyGroup = getStudyGroup(studyGroupId);
    User targetUser = userRepository.findById(targetUserId).orElseThrow(StudyMemberNotFoundException::new);

    return studyRestrictionRepository.save(StudyRestriction.builder()
        .studyGroup(studyGroup)
        .user(targetUser)
        .blacklisted(false)
        .blocked(false)
        .build());
  }

  private void validateKickTarget(StudyMember targetStudyMember, Long requestUserId) {
    if (targetStudyMember.getRole() == StudyMemberRole.OWNER || targetStudyMember.getUser().getId().equals(requestUserId)) {
      throw new StudyAccessDeniedException();
    }
  }
}
