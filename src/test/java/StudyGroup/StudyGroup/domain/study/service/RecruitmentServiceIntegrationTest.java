package StudyGroup.StudyGroup.domain.study.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import StudyGroup.StudyGroup.domain.study.dto.request.RecruitmentPostCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.request.StudyApplicationCreateRequestDto;
import StudyGroup.StudyGroup.domain.study.dto.response.RecruitmentPostResponseDto;
import StudyGroup.StudyGroup.domain.study.dto.response.StudyApplicationResponseDto;
import StudyGroup.StudyGroup.domain.study.entity.StudyGroup;
import StudyGroup.StudyGroup.domain.study.entity.StudyMember;
import StudyGroup.StudyGroup.domain.study.entity.StudyMemberRole;
import StudyGroup.StudyGroup.domain.study.entity.StudyStatus;
import StudyGroup.StudyGroup.domain.study.exception.StudyApplicationDuplicatedException;
import StudyGroup.StudyGroup.domain.study.exception.StudyCapacityExceededException;
import StudyGroup.StudyGroup.domain.study.repository.StudyApplicationRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyGroupRepository;
import StudyGroup.StudyGroup.domain.study.repository.StudyMemberRepository;
import StudyGroup.StudyGroup.domain.user.entity.User;
import StudyGroup.StudyGroup.domain.user.entity.UserProvider;
import StudyGroup.StudyGroup.domain.user.entity.UserRole;
import StudyGroup.StudyGroup.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RecruitmentServiceIntegrationTest {

  @Autowired
  private RecruitmentService recruitmentService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StudyGroupRepository studyGroupRepository;

  @Autowired
  private StudyMemberRepository studyMemberRepository;

  @Autowired
  private StudyApplicationRepository studyApplicationRepository;

  @Test
  void duplicateApplication_isBlocked() {
    User ownerUser = createUser("owner1@test.com", "owner1");
    User applicantUser = createUser("applicant1@test.com", "applicant1");
    StudyGroup studyGroup = createStudyGroup("Java Study", 5);
    addStudyMember(studyGroup, ownerUser, StudyMemberRole.OWNER);

    RecruitmentPostResponseDto recruitmentPostResponseDto = recruitmentService.createRecruitmentPost(
        ownerUser.getId(),
        studyGroup.getId(),
        new RecruitmentPostCreateRequestDto("백엔드 스터디 모집", "함께 공부할 분 모집합니다.")
    );

    recruitmentService.applyStudy(
        applicantUser.getId(),
        studyGroup.getId(),
        recruitmentPostResponseDto.recruitmentPostId(),
        new StudyApplicationCreateRequestDto("열심히 하겠습니다.")
    );

    assertThrows(
        StudyApplicationDuplicatedException.class,
        () -> recruitmentService.applyStudy(
            applicantUser.getId(),
            studyGroup.getId(),
            recruitmentPostResponseDto.recruitmentPostId(),
            new StudyApplicationCreateRequestDto("다시 신청합니다.")
        )
    );
  }

  @Test
  void adminCanApproveApplication() {
    User ownerUser = createUser("owner2@test.com", "owner2");
    User adminUser = createUser("admin2@test.com", "admin2");
    User applicantUser = createUser("applicant2@test.com", "applicant2");
    StudyGroup studyGroup = createStudyGroup("Spring Study", 5);
    addStudyMember(studyGroup, ownerUser, StudyMemberRole.OWNER);
    addStudyMember(studyGroup, adminUser, StudyMemberRole.ADMIN);

    RecruitmentPostResponseDto recruitmentPostResponseDto = recruitmentService.createRecruitmentPost(
        ownerUser.getId(),
        studyGroup.getId(),
        new RecruitmentPostCreateRequestDto("스프링 모집", "스프링 실전 스터디")
    );

    StudyApplicationResponseDto studyApplicationResponseDto = recruitmentService.applyStudy(
        applicantUser.getId(),
        studyGroup.getId(),
        recruitmentPostResponseDto.recruitmentPostId(),
        new StudyApplicationCreateRequestDto("백엔드 실무 역량을 키우고 싶습니다.")
    );

    recruitmentService.approveApplication(
        adminUser.getId(),
        studyGroup.getId(),
        recruitmentPostResponseDto.recruitmentPostId(),
        studyApplicationResponseDto.applicationId()
    );

    boolean exists = studyMemberRepository.findByStudyGroupIdAndUserId(studyGroup.getId(), applicantUser.getId()).isPresent();
    assertThat(exists).isTrue();
  }

  @Test
  void capacityExceeded_isBlocked() {
    User ownerUser = createUser("owner3@test.com", "owner3");
    User applicantUser1 = createUser("applicant3-1@test.com", "applicant31");
    User applicantUser2 = createUser("applicant3-2@test.com", "applicant32");
    StudyGroup studyGroup = createStudyGroup("Capacity Study", 2);
    addStudyMember(studyGroup, ownerUser, StudyMemberRole.OWNER);

    RecruitmentPostResponseDto recruitmentPostResponseDto = recruitmentService.createRecruitmentPost(
        ownerUser.getId(),
        studyGroup.getId(),
        new RecruitmentPostCreateRequestDto("정원 테스트 모집", "정원 제한 테스트")
    );

    StudyApplicationResponseDto application1 = recruitmentService.applyStudy(
        applicantUser1.getId(),
        studyGroup.getId(),
        recruitmentPostResponseDto.recruitmentPostId(),
        new StudyApplicationCreateRequestDto("신청1")
    );
    StudyApplicationResponseDto application2 = recruitmentService.applyStudy(
        applicantUser2.getId(),
        studyGroup.getId(),
        recruitmentPostResponseDto.recruitmentPostId(),
        new StudyApplicationCreateRequestDto("신청2")
    );

    recruitmentService.approveApplication(
        ownerUser.getId(),
        studyGroup.getId(),
        recruitmentPostResponseDto.recruitmentPostId(),
        application1.applicationId()
    );

    assertThrows(
        StudyCapacityExceededException.class,
        () -> recruitmentService.approveApplication(
            ownerUser.getId(),
            studyGroup.getId(),
            recruitmentPostResponseDto.recruitmentPostId(),
            application2.applicationId()
        )
    );
  }

  @Test
  void rejectedApplication_canReapply() {
    User ownerUser = createUser("owner4@test.com", "owner4");
    User applicantUser = createUser("applicant4@test.com", "applicant4");
    StudyGroup studyGroup = createStudyGroup("Retry Study", 4);
    addStudyMember(studyGroup, ownerUser, StudyMemberRole.OWNER);

    RecruitmentPostResponseDto recruitmentPostResponseDto = recruitmentService.createRecruitmentPost(
        ownerUser.getId(),
        studyGroup.getId(),
        new RecruitmentPostCreateRequestDto("재신청 테스트", "거절 후 재신청")
    );

    StudyApplicationResponseDto firstApplication = recruitmentService.applyStudy(
        applicantUser.getId(),
        studyGroup.getId(),
        recruitmentPostResponseDto.recruitmentPostId(),
        new StudyApplicationCreateRequestDto("첫 신청")
    );

    recruitmentService.rejectApplication(
        ownerUser.getId(),
        studyGroup.getId(),
        recruitmentPostResponseDto.recruitmentPostId(),
        firstApplication.applicationId()
    );

    StudyApplicationResponseDto secondApplication = recruitmentService.applyStudy(
        applicantUser.getId(),
        studyGroup.getId(),
        recruitmentPostResponseDto.recruitmentPostId(),
        new StudyApplicationCreateRequestDto("재신청")
    );

    assertThat(secondApplication.applicationId()).isEqualTo(firstApplication.applicationId());
    assertThat(studyApplicationRepository.findById(secondApplication.applicationId())).isPresent();
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

  private StudyGroup createStudyGroup(String name, int maxMemberCount) {
    StudyGroup studyGroup = StudyGroup.builder()
        .name(name)
        .description(name + " description")
        .maxMemberCount(maxMemberCount)
        .status(StudyStatus.RECRUITING)
        .build();

    return studyGroupRepository.save(studyGroup);
  }

  private void addStudyMember(StudyGroup studyGroup, User user, StudyMemberRole studyMemberRole) {
    StudyMember studyMember = StudyMember.builder()
        .studyGroup(studyGroup)
        .user(user)
        .role(studyMemberRole)
        .build();

    studyMemberRepository.save(studyMember);
  }
}
