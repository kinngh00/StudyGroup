package StudyGroup.StudyGroup.domain.study.service;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyAdminAssignRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyCreateResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyDetailResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudySummaryResponseDto;
import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;
import StudyGroup.StudyGroup.domain.study.entity.StudyMember;
import StudyGroup.StudyGroup.domain.study.entity.StudyMemberRole;
import StudyGroup.StudyGroup.domain.study.entity.StudyStatus;
import StudyGroup.StudyGroup.domain.study.exception.StudyAccessDeniedException;
import StudyGroup.StudyGroup.domain.study.exception.StudyGroupNotFoundException;
import StudyGroup.StudyGroup.domain.study.exception.StudyMemberNotFoundException;
import StudyGroup.StudyGroup.domain.study.repository.StudyGroupRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyMemberRepository;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

  private final StudyGroupRepository studyGroupRepository;
  private final StudyMemberRepository studyMemberRepository;
  private final UserRepository userRepository;
  private final StudyPermissionService studyPermissionService;

  @Transactional
  public StudyCreateResponseDto createStudy(Long requestUserId, StudyCreateRequestDto studyCreateRequestDto) {
    User requestUser = userRepository.findById(requestUserId)
        .orElseThrow(StudyAccessDeniedException::new);

    StudyGroup studyGroup = StudyGroup.builder()
        .name(studyCreateRequestDto.normalizedName())
        .description(studyCreateRequestDto.normalizedDescription())
        .maxMemberCount(studyCreateRequestDto.maxMemberCount())
        .status(StudyStatus.RECRUITING)
        .build();

    StudyGroup savedStudyGroup = studyGroupRepository.save(studyGroup);

    StudyMember ownerMember = StudyMember.builder()
        .studyGroup(savedStudyGroup)
        .user(requestUser)
        .role(StudyMemberRole.OWNER)
        .build();
    studyMemberRepository.save(ownerMember);

    return StudyCreateResponseDto.from(savedStudyGroup);
  }

  public StudyDetailResponseDto getStudyDetail(Long studyGroupId) {
    StudyGroup studyGroup = getStudyGroup(studyGroupId);
    long currentMemberCount = studyMemberRepository.countByStudyGroupId(studyGroupId);

    StudyMember ownerMember = studyMemberRepository.findByStudyGroupIdAndRole(studyGroupId, StudyMemberRole.OWNER)
        .orElseThrow(StudyGroupNotFoundException::new);

    return StudyDetailResponseDto.from(
        studyGroup,
        currentMemberCount,
        ownerMember.getUser().getId(),
        ownerMember.getUser().getName()
    );
  }

  public List<StudySummaryResponseDto> getStudies() {
    return studyGroupRepository.findAllByOrderByCreatedAtDesc().stream()
        .map(StudySummaryResponseDto::from)
        .toList();
  }

  @Transactional
  public StudyDetailResponseDto updateStudy(
      Long requestUserId,
      Long studyGroupId,
      StudyUpdateRequestDto studyUpdateRequestDto
  ) {
    StudyGroup studyGroup = getStudyGroup(studyGroupId);
    studyPermissionService.validateOwnerPermission(studyGroupId, requestUserId);

    StudyStatus updateStatus = studyUpdateRequestDto.status() == null
        ? studyGroup.getStatus()
        : studyUpdateRequestDto.status();

    studyGroup.update(
        studyUpdateRequestDto.normalizedName(),
        studyUpdateRequestDto.normalizedDescription(),
        studyUpdateRequestDto.maxMemberCount(),
        updateStatus
    );

    return getStudyDetail(studyGroupId);
  }

  @Transactional
  public void deleteStudy(Long requestUserId, Long studyGroupId) {
    getStudyGroup(studyGroupId);
    studyPermissionService.validateOwnerPermission(studyGroupId, requestUserId);

    studyMemberRepository.deleteByStudyGroupId(studyGroupId);
    studyGroupRepository.deleteById(studyGroupId);
  }

  @Transactional
  public void assignAdmin(Long requestUserId, Long studyGroupId, StudyAdminAssignRequestDto studyAdminAssignRequestDto) {
    getStudyGroup(studyGroupId);
    studyPermissionService.validateOwnerPermission(studyGroupId, requestUserId);

    User targetUser = userRepository.findById(studyAdminAssignRequestDto.userId())
        .orElseThrow(StudyMemberNotFoundException::new);

    StudyMember targetMember = studyMemberRepository.findByStudyGroupIdAndUserId(studyGroupId, targetUser.getId())
        .orElseThrow(StudyMemberNotFoundException::new);

    if (targetMember.getRole() == StudyMemberRole.OWNER) {
      throw new StudyAccessDeniedException();
    }

    targetMember.changeRole(StudyMemberRole.ADMIN);
  }

  @Transactional
  public void revokeAdmin(Long requestUserId, Long studyGroupId, Long targetUserId) {
    getStudyGroup(studyGroupId);
    studyPermissionService.validateOwnerPermission(studyGroupId, requestUserId);

    StudyMember targetMember = studyMemberRepository.findByStudyGroupIdAndUserId(studyGroupId, targetUserId)
        .orElseThrow(StudyMemberNotFoundException::new);

    if (targetMember.getRole() == StudyMemberRole.OWNER) {
      throw new StudyAccessDeniedException();
    }

    targetMember.changeRole(StudyMemberRole.MEMBER);
  }

  private StudyGroup getStudyGroup(Long studyGroupId) {
    return studyGroupRepository.findById(studyGroupId)
        .orElseThrow(StudyGroupNotFoundException::new);
  }
}
