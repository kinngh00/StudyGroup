package StudyGroup.StudyGroup.domain.study.service;

import StudyGroup.StudyGroup.domain.study.dto.request.RecruitmentPostCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.RecruitmentPostUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyApplicationCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.RecruitmentPostResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyApplicationResponseDto;
import StudyGroup.StudyGroup.domain.study.entity.ApplicationStatus;
import StudyGroup.StudyGroup.domain.study.entity.RecruitmentPost;
import StudyGroup.StudyGroup.domain.study.entity.RecruitmentPostStatus;
import StudyGroup.StudyGroup.domain.study.entity.StudyApplication;
import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;
import StudyGroup.StudyGroup.domain.study.entity.StudyMember;
import StudyGroup.StudyGroup.domain.study.entity.StudyMemberRole;
import StudyGroup.StudyGroup.domain.study.exception.AlreadyStudyMemberException;
import StudyGroup.StudyGroup.domain.study.exception.RecruitmentPostNotFoundException;
import StudyGroup.StudyGroup.domain.study.exception.RecruitmentNotOpenException;
import StudyGroup.StudyGroup.domain.study.exception.StudyAccessDeniedException;
import StudyGroup.StudyGroup.domain.study.exception.StudyApplicationDuplicatedException;
import StudyGroup.StudyGroup.domain.study.exception.StudyApplicationNotFoundException;
import StudyGroup.StudyGroup.domain.study.exception.StudyApplicationNotPendingException;
import StudyGroup.StudyGroup.domain.study.exception.StudyCapacityExceededException;
import StudyGroup.StudyGroup.domain.study.exception.StudyGroupNotFoundException;
import StudyGroup.StudyGroup.domain.study.repository.RecruitmentPostRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyApplicationRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyGroupRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyMemberRepository;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

  private final StudyGroupRepository studyGroupRepository;
  private final StudyMemberRepository studyMemberRepository;
  private final RecruitmentPostRepository recruitmentPostRepository;
  private final StudyApplicationRepository studyApplicationRepository;
  private final UserRepository userRepository;
  private final StudyPermissionService studyPermissionService;

  @Transactional
  public RecruitmentPostResponseDto createRecruitmentPost(
      Long requestUserId,
      Long studyGroupId,
      RecruitmentPostCreateRequestDto recruitmentPostCreateRequestDto
  ) {
    StudyGroup studyGroup = getStudyGroup(studyGroupId);
    studyPermissionService.validateManagerPermission(studyGroupId, requestUserId);
    User requestUser = getUser(requestUserId);

    RecruitmentPost recruitmentPost = RecruitmentPost.builder()
        .studyGroup(studyGroup)
        .authorUser(requestUser)
        .title(recruitmentPostCreateRequestDto.normalizedTitle())
        .content(recruitmentPostCreateRequestDto.normalizedContent())
        .status(RecruitmentPostStatus.OPEN)
        .build();

    RecruitmentPost savedRecruitmentPost = recruitmentPostRepository.save(recruitmentPost);
    return RecruitmentPostResponseDto.from(savedRecruitmentPost);
  }

  public RecruitmentPostResponseDto getRecruitmentPost(Long studyGroupId, Long recruitmentPostId) {
    RecruitmentPost recruitmentPost = getRecruitmentPostEntity(studyGroupId, recruitmentPostId);
    return RecruitmentPostResponseDto.from(recruitmentPost);
  }

  public List<RecruitmentPostResponseDto> getRecruitmentPosts(Long studyGroupId) {
    getStudyGroup(studyGroupId);
    return recruitmentPostRepository.findByStudyGroupIdOrderByCreatedAtDesc(studyGroupId).stream()
        .map(RecruitmentPostResponseDto::from)
        .toList();
  }

  @Transactional
  public RecruitmentPostResponseDto updateRecruitmentPost(
      Long requestUserId,
      Long studyGroupId,
      Long recruitmentPostId,
      RecruitmentPostUpdateRequestDto recruitmentPostUpdateRequestDto
  ) {
    RecruitmentPost recruitmentPost = getRecruitmentPostEntity(studyGroupId, recruitmentPostId);
    studyPermissionService.validatePostManagePermission(
        studyGroupId,
        requestUserId,
        recruitmentPost.getAuthorUser().getId()
    );

    RecruitmentPostStatus updateStatus = recruitmentPostUpdateRequestDto.status() == null
        ? recruitmentPost.getStatus()
        : recruitmentPostUpdateRequestDto.status();

    recruitmentPost.update(
        recruitmentPostUpdateRequestDto.normalizedTitle(),
        recruitmentPostUpdateRequestDto.normalizedContent(),
        updateStatus
    );

    return RecruitmentPostResponseDto.from(recruitmentPost);
  }

  @Transactional
  public void deleteRecruitmentPost(Long requestUserId, Long studyGroupId, Long recruitmentPostId) {
    RecruitmentPost recruitmentPost = getRecruitmentPostEntity(studyGroupId, recruitmentPostId);
    studyPermissionService.validatePostManagePermission(
        studyGroupId,
        requestUserId,
        recruitmentPost.getAuthorUser().getId()
    );
    studyApplicationRepository.deleteByRecruitmentPostId(recruitmentPostId);
    recruitmentPostRepository.delete(recruitmentPost);
  }

  @Transactional
  public StudyApplicationResponseDto applyStudy(
      Long requestUserId,
      Long studyGroupId,
      Long recruitmentPostId,
      StudyApplicationCreateRequestDto studyApplicationCreateRequestDto
  ) {
    RecruitmentPost recruitmentPost = getRecruitmentPostEntity(studyGroupId, recruitmentPostId);
    User requestUser = getUser(requestUserId);

    if (recruitmentPost.getStatus() != RecruitmentPostStatus.OPEN) {
      throw new RecruitmentNotOpenException();
    }

    if (studyMemberRepository.findByStudyGroupIdAndUserId(studyGroupId, requestUserId).isPresent()) {
      throw new AlreadyStudyMemberException();
    }

    if (studyApplicationRepository.existsByStudyGroupIdAndApplicantUserIdAndStatusIn(
        studyGroupId,
        requestUserId,
        EnumSet.of(ApplicationStatus.PENDING, ApplicationStatus.APPROVED)
    )) {
      throw new StudyApplicationDuplicatedException();
    }

    StudyApplication rejectedApplication = studyApplicationRepository
        .findFirstByStudyGroupIdAndApplicantUserIdAndStatusOrderByCreatedAtDesc(
            studyGroupId,
            requestUserId,
            ApplicationStatus.REJECTED
        )
        .orElse(null);

    if (rejectedApplication != null) {
      rejectedApplication.reapply(recruitmentPost, studyApplicationCreateRequestDto.normalizedMotivation());
      return StudyApplicationResponseDto.from(rejectedApplication);
    }

    StudyApplication studyApplication = StudyApplication.builder()
        .recruitmentPost(recruitmentPost)
        .studyGroup(recruitmentPost.getStudyGroup())
        .applicantUser(requestUser)
        .motivation(studyApplicationCreateRequestDto.normalizedMotivation())
        .status(ApplicationStatus.PENDING)
        .build();

    try {
      StudyApplication savedApplication = studyApplicationRepository.save(studyApplication);
      return StudyApplicationResponseDto.from(savedApplication);
    } catch (DataIntegrityViolationException exception) {
      throw new StudyApplicationDuplicatedException();
    }
  }

  public List<StudyApplicationResponseDto> getApplications(Long requestUserId, Long studyGroupId, Long recruitmentPostId) {
    studyPermissionService.validateManagerPermission(studyGroupId, requestUserId);
    getRecruitmentPostEntity(studyGroupId, recruitmentPostId);

    return studyApplicationRepository.findByRecruitmentPostIdOrderByCreatedAtDesc(recruitmentPostId).stream()
        .map(StudyApplicationResponseDto::from)
        .toList();
  }

  @Transactional
  public StudyApplicationResponseDto approveApplication(
      Long requestUserId,
      Long studyGroupId,
      Long recruitmentPostId,
      Long applicationId
  ) {
    studyPermissionService.validateManagerPermission(studyGroupId, requestUserId);
    StudyApplication studyApplication = getStudyApplication(studyGroupId, recruitmentPostId, applicationId);
    StudyGroup lockedStudyGroup = getStudyGroupWithLock(studyGroupId);

    if (studyApplication.getStatus() != ApplicationStatus.PENDING) {
      throw new StudyApplicationNotPendingException();
    }

    long currentMemberCount = studyMemberRepository.countByStudyGroupId(studyGroupId);
    if (currentMemberCount >= lockedStudyGroup.getMaxMemberCount()) {
      throw new StudyCapacityExceededException();
    }

    Long applicantUserId = studyApplication.getApplicantUser().getId();
    if (studyMemberRepository.findByStudyGroupIdAndUserId(studyGroupId, applicantUserId).isPresent()) {
      throw new AlreadyStudyMemberException();
    }

    try {
      StudyMember studyMember = StudyMember.builder()
          .studyGroup(studyApplication.getStudyGroup())
          .user(studyApplication.getApplicantUser())
          .role(StudyMemberRole.MEMBER)
          .build();
      studyMemberRepository.save(studyMember);
    } catch (DataIntegrityViolationException exception) {
      throw new AlreadyStudyMemberException();
    }

    studyApplication.approve();

    long updatedMemberCount = currentMemberCount + 1;
    if (updatedMemberCount >= lockedStudyGroup.getMaxMemberCount()) {
      RecruitmentPost recruitmentPost = studyApplication.getRecruitmentPost();
      recruitmentPost.update(
          recruitmentPost.getTitle(),
          recruitmentPost.getContent(),
          RecruitmentPostStatus.CLOSED
      );
    }

    return StudyApplicationResponseDto.from(studyApplication);
  }

  @Transactional
  public StudyApplicationResponseDto rejectApplication(
      Long requestUserId,
      Long studyGroupId,
      Long recruitmentPostId,
      Long applicationId
  ) {
    studyPermissionService.validateManagerPermission(studyGroupId, requestUserId);
    StudyApplication studyApplication = getStudyApplication(studyGroupId, recruitmentPostId, applicationId);

    if (studyApplication.getStatus() != ApplicationStatus.PENDING) {
      throw new StudyApplicationNotPendingException();
    }

    studyApplication.reject();
    return StudyApplicationResponseDto.from(studyApplication);
  }

  private StudyGroup getStudyGroup(Long studyGroupId) {
    return studyGroupRepository.findById(studyGroupId)
        .orElseThrow(StudyGroupNotFoundException::new);
  }

  private StudyGroup getStudyGroupWithLock(Long studyGroupId) {
    return studyGroupRepository.findWithLockById(studyGroupId)
        .orElseThrow(StudyGroupNotFoundException::new);
  }

  private User getUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(StudyAccessDeniedException::new);
  }

  private RecruitmentPost getRecruitmentPostEntity(Long studyGroupId, Long recruitmentPostId) {
    getStudyGroup(studyGroupId);
    return recruitmentPostRepository.findByIdAndStudyGroupId(recruitmentPostId, studyGroupId)
        .orElseThrow(RecruitmentPostNotFoundException::new);
  }

  private StudyApplication getStudyApplication(Long studyGroupId, Long recruitmentPostId, Long applicationId) {
    return studyApplicationRepository.findByIdAndRecruitmentPostIdAndStudyGroupId(
            applicationId,
            recruitmentPostId,
            studyGroupId
        )
        .orElseThrow(StudyApplicationNotFoundException::new);
  }
}
