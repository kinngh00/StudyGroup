package StudyGroup.StudyGroup.domain.study.service;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyNoticeCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyNoticeUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyNoticeResponseDto;
import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;
import StudyGroup.StudyGroup.domain.study.entity.StudyNotice;
import StudyGroup.StudyGroup.domain.study.exception.StudyAccessDeniedException;
import StudyGroup.StudyGroup.domain.study.exception.StudyGroupNotFoundException;
import StudyGroup.StudyGroup.domain.study.exception.StudyNoticeNotFoundException;
import StudyGroup.StudyGroup.domain.study.repository.StudyGroupRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyNoticeRepository;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyNoticeService {

  private final StudyGroupRepository studyGroupRepository;
  private final StudyNoticeRepository studyNoticeRepository;
  private final UserRepository userRepository;
  private final StudyPermissionService studyPermissionService;

  @Transactional
  public StudyNoticeResponseDto createNotice(
      Long requestUserId,
      Long studyGroupId,
      StudyNoticeCreateRequestDto studyNoticeCreateRequestDto
  ) {
    StudyGroup studyGroup = getStudyGroup(studyGroupId);
    studyPermissionService.validateNoticeManagePermission(studyGroupId, requestUserId);
    User requestUser = getUser(requestUserId);

    StudyNotice studyNotice = StudyNotice.builder()
        .studyGroup(studyGroup)
        .authorUser(requestUser)
        .title(studyNoticeCreateRequestDto.normalizedTitle())
        .content(studyNoticeCreateRequestDto.normalizedContent())
        .pinned(studyNoticeCreateRequestDto.pinned())
        .build();

    StudyNotice savedStudyNotice = studyNoticeRepository.save(studyNotice);
    return StudyNoticeResponseDto.from(savedStudyNotice);
  }

  public List<StudyNoticeResponseDto> getNotices(Long requestUserId, Long studyGroupId) {
    studyPermissionService.validateStudyMemberPermission(studyGroupId, requestUserId);
    return studyNoticeRepository.findByStudyGroupIdOrderByPinnedDescCreatedAtDesc(studyGroupId).stream()
        .map(StudyNoticeResponseDto::from)
        .toList();
  }

  public StudyNoticeResponseDto getNotice(Long requestUserId, Long studyGroupId, Long studyNoticeId) {
    studyPermissionService.validateStudyMemberPermission(studyGroupId, requestUserId);
    StudyNotice studyNotice = getStudyNotice(studyGroupId, studyNoticeId);
    return StudyNoticeResponseDto.from(studyNotice);
  }

  @Transactional
  public StudyNoticeResponseDto updateNotice(
      Long requestUserId,
      Long studyGroupId,
      Long studyNoticeId,
      StudyNoticeUpdateRequestDto studyNoticeUpdateRequestDto
  ) {
    studyPermissionService.validateNoticeManagePermission(studyGroupId, requestUserId);
    StudyNotice studyNotice = getStudyNotice(studyGroupId, studyNoticeId);

    studyNotice.update(
        studyNoticeUpdateRequestDto.normalizedTitle(),
        studyNoticeUpdateRequestDto.normalizedContent(),
        studyNoticeUpdateRequestDto.pinned()
    );

    return StudyNoticeResponseDto.from(studyNotice);
  }

  @Transactional
  public void deleteNotice(Long requestUserId, Long studyGroupId, Long studyNoticeId) {
    studyPermissionService.validateNoticeManagePermission(studyGroupId, requestUserId);
    StudyNotice studyNotice = getStudyNotice(studyGroupId, studyNoticeId);
    studyNoticeRepository.delete(studyNotice);
  }

  private StudyGroup getStudyGroup(Long studyGroupId) {
    return studyGroupRepository.findById(studyGroupId)
        .orElseThrow(StudyGroupNotFoundException::new);
  }

  private User getUser(Long requestUserId) {
    return userRepository.findById(requestUserId)
        .orElseThrow(StudyAccessDeniedException::new);
  }

  private StudyNotice getStudyNotice(Long studyGroupId, Long studyNoticeId) {
    return studyNoticeRepository.findByIdAndStudyGroupId(studyNoticeId, studyGroupId)
        .orElseThrow(StudyNoticeNotFoundException::new);
  }
}
