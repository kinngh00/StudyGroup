package StudyGroup.StudyGroup.domain.study.service;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyScheduleCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyScheduleUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyScheduleResponseDto;
import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;
import StudyGroup.StudyGroup.domain.study.entity.StudySchedule;
import StudyGroup.StudyGroup.domain.study.exception.StudyGroupNotFoundException;
import StudyGroup.StudyGroup.domain.study.exception.StudyScheduleNotFoundException;
import StudyGroup.StudyGroup.domain.study.repository.StudyGroupRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyScheduleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyScheduleService {

  private final StudyGroupRepository studyGroupRepository;
  private final StudyScheduleRepository studyScheduleRepository;
  private final StudyPermissionService studyPermissionService;

  @Transactional
  public StudyScheduleResponseDto createSchedule(
      Long requestUserId,
      Long studyGroupId,
      StudyScheduleCreateRequestDto studyScheduleCreateRequestDto
  ) {
    StudyGroup studyGroup = getStudyGroup(studyGroupId);
    studyPermissionService.validateScheduleManagePermission(studyGroupId, requestUserId);

    StudySchedule studySchedule = StudySchedule.builder()
        .studyGroup(studyGroup)
        .title(studyScheduleCreateRequestDto.normalizedTitle())
        .description(studyScheduleCreateRequestDto.normalizedDescription())
        .scheduledAt(studyScheduleCreateRequestDto.scheduledAt())
        .build();

    StudySchedule savedStudySchedule = studyScheduleRepository.save(studySchedule);
    return StudyScheduleResponseDto.from(savedStudySchedule);
  }

  public List<StudyScheduleResponseDto> getSchedules(Long requestUserId, Long studyGroupId) {
    studyPermissionService.validateStudyMemberPermission(studyGroupId, requestUserId);
    return studyScheduleRepository.findByStudyGroupIdOrderByScheduledAtAsc(studyGroupId).stream()
        .map(StudyScheduleResponseDto::from)
        .toList();
  }

  @Transactional
  public StudyScheduleResponseDto updateSchedule(
      Long requestUserId,
      Long studyGroupId,
      Long studyScheduleId,
      StudyScheduleUpdateRequestDto studyScheduleUpdateRequestDto
  ) {
    studyPermissionService.validateScheduleManagePermission(studyGroupId, requestUserId);
    StudySchedule studySchedule = getStudySchedule(studyGroupId, studyScheduleId);

    studySchedule.update(
        studyScheduleUpdateRequestDto.normalizedTitle(),
        studyScheduleUpdateRequestDto.normalizedDescription(),
        studyScheduleUpdateRequestDto.scheduledAt()
    );

    return StudyScheduleResponseDto.from(studySchedule);
  }

  @Transactional
  public void deleteSchedule(Long requestUserId, Long studyGroupId, Long studyScheduleId) {
    studyPermissionService.validateScheduleManagePermission(studyGroupId, requestUserId);
    StudySchedule studySchedule = getStudySchedule(studyGroupId, studyScheduleId);
    studyScheduleRepository.delete(studySchedule);
  }

  private StudyGroup getStudyGroup(Long studyGroupId) {
    return studyGroupRepository.findById(studyGroupId)
        .orElseThrow(StudyGroupNotFoundException::new);
  }

  private StudySchedule getStudySchedule(Long studyGroupId, Long studyScheduleId) {
    return studyScheduleRepository.findByIdAndStudyGroupId(studyScheduleId, studyGroupId)
        .orElseThrow(StudyScheduleNotFoundException::new);
  }
}
