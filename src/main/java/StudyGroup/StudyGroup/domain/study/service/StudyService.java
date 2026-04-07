package StudyGroup.StudyGroup.domain.study.service;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyAdminAssignRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyAdminPermissionUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyAdminPermissionResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyCreateResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyDetailResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyMemberResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyRoleResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudySummaryResponseDto;
import StudyGroup.StudyGroup.domain.study.entity.StudyAdminPermission;
import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;
import StudyGroup.StudyGroup.domain.study.entity.StudyMember;
import StudyGroup.StudyGroup.domain.study.entity.StudyMemberRole;
import StudyGroup.StudyGroup.domain.study.entity.StudyStatus;
import StudyGroup.StudyGroup.domain.study.exception.StudyAccessDeniedException;
import StudyGroup.StudyGroup.domain.study.exception.StudyGroupNotFoundException;
import StudyGroup.StudyGroup.domain.study.exception.StudyMemberNotFoundException;
import StudyGroup.StudyGroup.domain.study.repository.StudyAdminPermissionRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyApplicationRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyGroupRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyMemberRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyNoticeRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyReportRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyRestrictionRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyScheduleRepository;
import StudyGroup.StudyGroup.domain.study.repository.RecruitmentPostRepository;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

  private final StudyGroupRepository studyGroupRepository;
  private final StudyMemberRepository studyMemberRepository;
  private final StudyAdminPermissionRepository studyAdminPermissionRepository;
  private final StudyApplicationRepository studyApplicationRepository;
  private final RecruitmentPostRepository recruitmentPostRepository;
  private final StudyScheduleRepository studyScheduleRepository;
  private final StudyRestrictionRepository studyRestrictionRepository;
  private final StudyNoticeRepository studyNoticeRepository;
  private final StudyReportRepository studyReportRepository;
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
    return getStudies(null, null, null);
  }

  public List<StudySummaryResponseDto> getStudies(String nameKeyword, StudyStatus status, Boolean isClosed) {
    StudyStatus searchStatus = resolveSearchStatus(status, isClosed);

    return studyGroupRepository.search(nameKeyword, searchStatus).stream()
        .map(StudySummaryResponseDto::from)
        .toList();
  }

  public StudyRoleResponseDto getMyRole(Long requestUserId, Long studyGroupId) {
    StudyMember studyMember = studyMemberRepository.findByStudyGroupIdAndUserId(studyGroupId, requestUserId)
        .orElse(null);

    if (studyMember == null) {
      return new StudyRoleResponseDto("NONE");
    }

    return new StudyRoleResponseDto(studyMember.getRole().name());
  }

  public List<StudyMemberResponseDto> getStudyMembers(Long requestUserId, Long studyGroupId) {
    getStudyGroup(studyGroupId);
    studyPermissionService.validateStudyMemberPermission(studyGroupId, requestUserId);

    Map<Long, StudyAdminPermission> permissionByUserId = studyAdminPermissionRepository.findByStudyGroupId(studyGroupId)
        .stream()
        .collect(Collectors.toMap(permission -> permission.getUser().getId(), Function.identity()));

    return studyMemberRepository.findByStudyGroupIdOrderByJoinedAtAsc(studyGroupId).stream()
        .map(studyMember -> StudyMemberResponseDto.from(
            studyMember,
            permissionByUserId.get(studyMember.getUser().getId())
        ))
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

    studyReportRepository.deleteByStudyGroupId(studyGroupId);
    studyNoticeRepository.deleteByStudyGroupId(studyGroupId);
    studyScheduleRepository.deleteByStudyGroupId(studyGroupId);
    studyApplicationRepository.deleteByStudyGroupId(studyGroupId);
    recruitmentPostRepository.deleteByStudyGroupId(studyGroupId);
    studyRestrictionRepository.deleteByStudyGroupId(studyGroupId);
    studyAdminPermissionRepository.deleteByStudyGroupId(studyGroupId);
    studyMemberRepository.deleteByStudyGroupId(studyGroupId);
    studyGroupRepository.deleteById(studyGroupId);
  }

  @Transactional
  public StudyAdminPermissionResponseDto assignAdmin(
      Long requestUserId,
      Long studyGroupId,
      StudyAdminAssignRequestDto studyAdminAssignRequestDto
  ) {
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
    StudyAdminPermission studyAdminPermission = upsertAdminPermission(
        studyGroupId,
        targetUser,
        studyAdminAssignRequestDto.recruitmentWrite(),
        studyAdminAssignRequestDto.recruitmentApprove(),
        studyAdminAssignRequestDto.memberManage(),
        studyAdminAssignRequestDto.scheduleManage(),
        studyAdminAssignRequestDto.noticeManage()
    );
    return StudyAdminPermissionResponseDto.from(studyAdminPermission);
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
    studyAdminPermissionRepository.deleteByStudyGroupIdAndUserId(studyGroupId, targetUserId);
  }

  @Transactional
  public StudyAdminPermissionResponseDto updateAdminPermission(
      Long requestUserId,
      Long studyGroupId,
      Long targetUserId,
      StudyAdminPermissionUpdateRequestDto studyAdminPermissionUpdateRequestDto
  ) {
    getStudyGroup(studyGroupId);
    studyPermissionService.validateOwnerPermission(studyGroupId, requestUserId);

    StudyMember targetMember = studyMemberRepository.findByStudyGroupIdAndUserId(studyGroupId, targetUserId)
        .orElseThrow(StudyMemberNotFoundException::new);

    if (targetMember.getRole() != StudyMemberRole.ADMIN) {
      throw new StudyAccessDeniedException();
    }

    User targetUser = targetMember.getUser();
    StudyAdminPermission studyAdminPermission = upsertAdminPermission(
        studyGroupId,
        targetUser,
        studyAdminPermissionUpdateRequestDto.recruitmentWrite(),
        studyAdminPermissionUpdateRequestDto.recruitmentApprove(),
        studyAdminPermissionUpdateRequestDto.memberManage(),
        studyAdminPermissionUpdateRequestDto.scheduleManage(),
        studyAdminPermissionUpdateRequestDto.noticeManage()
    );
    return StudyAdminPermissionResponseDto.from(studyAdminPermission);
  }

  private StudyGroup getStudyGroup(Long studyGroupId) {
    return studyGroupRepository.findById(studyGroupId)
        .orElseThrow(StudyGroupNotFoundException::new);
  }

  private StudyStatus resolveSearchStatus(StudyStatus status, Boolean isClosed) {
    if (status != null) {
      return status;
    }

    if (isClosed == null) {
      return null;
    }

    return isClosed ? StudyStatus.RECRUITMENT_CLOSED : StudyStatus.RECRUITING;
  }

  private StudyAdminPermission upsertAdminPermission(
      Long studyGroupId,
      User targetUser,
      boolean recruitmentWrite,
      boolean recruitmentApprove,
      boolean memberManage,
      boolean scheduleManage,
      boolean noticeManage
  ) {
    StudyGroup studyGroup = getStudyGroup(studyGroupId);
    StudyAdminPermission studyAdminPermission = studyAdminPermissionRepository.findByStudyGroupIdAndUserId(
        studyGroupId,
        targetUser.getId()
    ).orElse(null);

    if (studyAdminPermission == null) {
      return studyAdminPermissionRepository.save(StudyAdminPermission.builder()
          .studyGroup(studyGroup)
          .user(targetUser)
          .recruitmentWrite(recruitmentWrite)
          .recruitmentApprove(recruitmentApprove)
          .memberManage(memberManage)
          .scheduleManage(scheduleManage)
          .noticeManage(noticeManage)
          .build());
    }

    studyAdminPermission.update(recruitmentWrite, recruitmentApprove, memberManage, scheduleManage, noticeManage);
    return studyAdminPermission;
  }
}
