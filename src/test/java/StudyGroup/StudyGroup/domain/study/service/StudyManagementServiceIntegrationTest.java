package StudyGroup.StudyGroup.domain.study.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import StudyGroup.StudyGroup.domain.study.dto.request.RecruitmentPostCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyApplicationCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyRestrictionRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyScheduleCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyScheduleUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyUpdateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.RecruitmentPostResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyScheduleResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudySummaryResponseDto;
import StudyGroup.StudyGroup.domain.study.entity.StudyStatus;
import StudyGroup.StudyGroup.domain.study.exception.StudyUserBlacklistedException;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.entity.UserProvider;
import StudyGroup.StudyGroup.domain.user.entity.UserRole;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StudyManagementServiceIntegrationTest {

  @Autowired
  private StudyService studyService;

  @Autowired
  private RecruitmentService recruitmentService;

  @Autowired
  private StudyMemberManagementService studyMemberManagementService;

  @Autowired
  private StudyScheduleService studyScheduleService;

  @Autowired
  private UserRepository userRepository;

  @Test
  void blacklistedUser_cannotApplyStudy() {
    User ownerUser = createUser("owner-black@test.com", "owner-black");
    User applicantUser = createUser("applicant-black@test.com", "applicant-black");

    Long studyGroupId = studyService.createStudy(
        ownerUser.getId(),
        new StudyCreateRequestDto("Blacklist Study", "Blacklist policy test", 10)
    ).studyGroupId();

    RecruitmentPostResponseDto recruitmentPostResponseDto = recruitmentService.createRecruitmentPost(
        ownerUser.getId(),
        studyGroupId,
        new RecruitmentPostCreateRequestDto("Recruitment", "Recruitment content")
    );

    studyMemberManagementService.addBlacklist(
        ownerUser.getId(),
        studyGroupId,
        applicantUser.getId(),
        new StudyRestrictionRequestDto("Repeated policy violation")
    );

    assertThrows(
        StudyUserBlacklistedException.class,
        () -> recruitmentService.applyStudy(
            applicantUser.getId(),
            studyGroupId,
            recruitmentPostResponseDto.recruitmentPostId(),
            new StudyApplicationCreateRequestDto("Please accept my application.")
        )
    );
  }

  @Test
  void studySchedule_crudWorks() {
    User ownerUser = createUser("owner-schedule@test.com", "owner-schedule");

    Long studyGroupId = studyService.createStudy(
        ownerUser.getId(),
        new StudyCreateRequestDto("Schedule Study", "Schedule management test", 10)
    ).studyGroupId();

    LocalDateTime firstScheduleAt = LocalDateTime.now().plusDays(1);
    StudyScheduleResponseDto createdSchedule = studyScheduleService.createSchedule(
        ownerUser.getId(),
        studyGroupId,
        new StudyScheduleCreateRequestDto("Week 1 Meeting", "Orientation", firstScheduleAt)
    );

    List<StudyScheduleResponseDto> scheduleList = studyScheduleService.getSchedules(ownerUser.getId(), studyGroupId);
    assertThat(scheduleList).hasSize(1);
    assertThat(scheduleList.get(0).studyScheduleId()).isEqualTo(createdSchedule.studyScheduleId());

    LocalDateTime updatedScheduleAt = LocalDateTime.now().plusDays(2);
    StudyScheduleResponseDto updatedSchedule = studyScheduleService.updateSchedule(
        ownerUser.getId(),
        studyGroupId,
        createdSchedule.studyScheduleId(),
        new StudyScheduleUpdateRequestDto("Week 1 Updated", "Updated orientation", updatedScheduleAt)
    );
    assertThat(updatedSchedule.title()).isEqualTo("Week 1 Updated");

    studyScheduleService.deleteSchedule(ownerUser.getId(), studyGroupId, createdSchedule.studyScheduleId());
    assertThat(studyScheduleService.getSchedules(ownerUser.getId(), studyGroupId)).isEmpty();
  }

  @Test
  void studySearch_worksByKeywordAndStatus() {
    User ownerUser = createUser("owner-search@test.com", "owner-search");

    Long recruitingStudyGroupId = studyService.createStudy(
        ownerUser.getId(),
        new StudyCreateRequestDto("Java Study", "Java backend study", 10)
    ).studyGroupId();
    Long closedStudyGroupId = studyService.createStudy(
        ownerUser.getId(),
        new StudyCreateRequestDto("Data Study", "Data analysis study", 10)
    ).studyGroupId();

    studyService.updateStudy(
        ownerUser.getId(),
        closedStudyGroupId,
        new StudyUpdateRequestDto("Data Study", "Data analysis study", 10, StudyStatus.RECRUITMENT_CLOSED)
    );

    List<StudySummaryResponseDto> keywordResult = studyService.getStudies("Java", null, null);
    assertThat(keywordResult).hasSize(1);
    assertThat(keywordResult.get(0).studyGroupId()).isEqualTo(recruitingStudyGroupId);

    List<StudySummaryResponseDto> closedResult = studyService.getStudies(null, null, true);
    assertThat(closedResult).hasSize(1);
    assertThat(closedResult.get(0).studyGroupId()).isEqualTo(closedStudyGroupId);

    List<StudySummaryResponseDto> statusResult = studyService.getStudies(null, StudyStatus.RECRUITING, null);
    assertThat(statusResult)
        .anyMatch(studySummaryResponseDto -> studySummaryResponseDto.studyGroupId().equals(recruitingStudyGroupId));
  }

  private User createUser(String email, String name) {
    User user = User.builder()
        .email(email)
        .password("encodedPassword")
        .name(name)
        .role(UserRole.USER)
        .provider(UserProvider.LOCAL)
        .build();

    return userRepository.save(user);
  }
}
