package StudyGroup.StudyGroup.domain.study.service;

import StudyGroup.StudyGroup.domain.study.dto.request.StudyCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyCreateResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyDetailResponseDto;
import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;
import StudyGroup.StudyGroup.domain.study.entity.StudyMember;
import StudyGroup.StudyGroup.domain.study.entity.StudyMemberRole;
import StudyGroup.StudyGroup.domain.study.entity.StudyStatus;
import StudyGroup.StudyGroup.domain.study.exception.StudyAccessDeniedException;
import StudyGroup.StudyGroup.domain.study.exception.StudyGroupNotFoundException;
import StudyGroup.StudyGroup.domain.study.repository.StudyGroupRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyMemberRepository;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
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

  @Transactional
  public StudyCreateResponseDto createStudy(Long requestUserId, StudyCreateRequestDto studyCreateRequestDto) {
    User requestUser = userRepository.findById(requestUserId)
        .orElseThrow(StudyAccessDeniedException::new);

    StudyGroup studyGroup = StudyGroup.builder()
        .name(studyCreateRequestDto.name())
        .description(studyCreateRequestDto.description())
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
    StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
        .orElseThrow(StudyGroupNotFoundException::new);

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

  public Long extractUserId(Object principal) {
    if (principal == null) {
      throw new StudyAccessDeniedException();
    }

    try {
      return Long.parseLong(principal.toString());
    } catch (NumberFormatException exception) {
      throw new StudyAccessDeniedException();
    }
  }
}
