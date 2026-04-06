package StudyGroup.StudyGroup.domain.study.service;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyReportCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyReportResponseDto;
import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;
import StudyGroup.StudyGroup.domain.study.entity.StudyReport;
import StudyGroup.StudyGroup.domain.study.entity.StudyReportStatus;
import StudyGroup.StudyGroup.domain.study.exception.StudyAccessDeniedException;
import StudyGroup.StudyGroup.domain.study.exception.StudyGroupNotFoundException;
import StudyGroup.StudyGroup.domain.study.exception.StudyMemberNotFoundException;
import StudyGroup.StudyGroup.domain.study.exception.StudyReportAlreadyResolvedException;
import StudyGroup.StudyGroup.domain.study.exception.StudyReportNotFoundException;
import StudyGroup.StudyGroup.domain.study.repository.StudyGroupRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyMemberRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyReportRepository;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyReportService {

  private final StudyGroupRepository studyGroupRepository;
  private final StudyMemberRepository studyMemberRepository;
  private final StudyReportRepository studyReportRepository;
  private final UserRepository userRepository;
  private final StudyPermissionService studyPermissionService;

  @Transactional
  public StudyReportResponseDto createReport(
      Long requestUserId,
      Long studyGroupId,
      StudyReportCreateRequestDto studyReportCreateRequestDto
  ) {
    StudyGroup studyGroup = getStudyGroup(studyGroupId);
    studyPermissionService.validateStudyMemberPermission(studyGroupId, requestUserId);
    User requestUser = getRequestUser(requestUserId);

    User targetUser = null;
    if (studyReportCreateRequestDto.targetUserId() != null) {
      Long targetUserId = studyReportCreateRequestDto.targetUserId();
      if (studyMemberRepository.findByStudyGroupIdAndUserId(studyGroupId, targetUserId).isEmpty()) {
        throw new StudyMemberNotFoundException();
      }
      targetUser = getTargetUser(targetUserId);
    }

    StudyReport studyReport = StudyReport.builder()
        .studyGroup(studyGroup)
        .reporterUser(requestUser)
        .targetUser(targetUser)
        .content(studyReportCreateRequestDto.normalizedContent())
        .status(StudyReportStatus.PENDING)
        .build();

    StudyReport savedStudyReport = studyReportRepository.save(studyReport);
    return StudyReportResponseDto.from(savedStudyReport);
  }

  public List<StudyReportResponseDto> getReports(Long requestUserId, Long studyGroupId) {
    studyPermissionService.validateMemberManagePermission(studyGroupId, requestUserId);
    return studyReportRepository.findByStudyGroupIdOrderByCreatedAtDesc(studyGroupId).stream()
        .map(StudyReportResponseDto::from)
        .toList();
  }

  @Transactional
  public StudyReportResponseDto resolveReport(Long requestUserId, Long studyGroupId, Long studyReportId) {
    studyPermissionService.validateMemberManagePermission(studyGroupId, requestUserId);
    StudyReport studyReport = getStudyReport(studyGroupId, studyReportId);

    if (studyReport.getStatus() == StudyReportStatus.RESOLVED) {
      throw new StudyReportAlreadyResolvedException();
    }

    User resolverUser = getRequestUser(requestUserId);
    studyReport.resolve(resolverUser);
    return StudyReportResponseDto.from(studyReport);
  }

  private StudyGroup getStudyGroup(Long studyGroupId) {
    return studyGroupRepository.findById(studyGroupId)
        .orElseThrow(StudyGroupNotFoundException::new);
  }

  private User getRequestUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(StudyAccessDeniedException::new);
  }

  private User getTargetUser(Long targetUserId) {
    return userRepository.findById(targetUserId)
        .orElseThrow(StudyMemberNotFoundException::new);
  }

  private StudyReport getStudyReport(Long studyGroupId, Long studyReportId) {
    return studyReportRepository.findByIdAndStudyGroupId(studyReportId, studyGroupId)
        .orElseThrow(StudyReportNotFoundException::new);
  }
}
