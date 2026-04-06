package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyErrorCode implements ExceptionCode {
  STUDY_GROUP_NOT_FOUND("STUDY_GROUP_NOT_FOUND", HttpStatus.NOT_FOUND, "Study group not found."),
  STUDY_ACCESS_DENIED("STUDY_ACCESS_DENIED", HttpStatus.FORBIDDEN, "Study access denied."),
  STUDY_MEMBER_NOT_FOUND("STUDY_MEMBER_NOT_FOUND", HttpStatus.NOT_FOUND, "Study member not found."),
  STUDY_SCHEDULE_NOT_FOUND("STUDY_SCHEDULE_NOT_FOUND", HttpStatus.NOT_FOUND, "Study schedule not found."),
  STUDY_NOTICE_NOT_FOUND("STUDY_NOTICE_NOT_FOUND", HttpStatus.NOT_FOUND, "Study notice not found."),
  STUDY_REPORT_NOT_FOUND("STUDY_REPORT_NOT_FOUND", HttpStatus.NOT_FOUND, "Study report not found."),
  STUDY_REPORT_ALREADY_RESOLVED("STUDY_REPORT_ALREADY_RESOLVED", HttpStatus.BAD_REQUEST, "Study report is already resolved."),
  STUDY_USER_BLOCKED("STUDY_USER_BLOCKED", HttpStatus.FORBIDDEN, "User is blocked in this study."),
  STUDY_USER_BLACKLISTED("STUDY_USER_BLACKLISTED", HttpStatus.FORBIDDEN, "User is blacklisted in this study."),
  RECRUITMENT_POST_NOT_FOUND("RECRUITMENT_POST_NOT_FOUND", HttpStatus.NOT_FOUND, "Recruitment post not found."),
  STUDY_APPLICATION_NOT_FOUND("STUDY_APPLICATION_NOT_FOUND", HttpStatus.NOT_FOUND, "Study application not found."),
  STUDY_APPLICATION_DUPLICATED("STUDY_APPLICATION_DUPLICATED", HttpStatus.CONFLICT, "Study application already exists."),
  STUDY_APPLICATION_NOT_PENDING("STUDY_APPLICATION_NOT_PENDING", HttpStatus.BAD_REQUEST, "Study application is not pending."),
  STUDY_CAPACITY_EXCEEDED("STUDY_CAPACITY_EXCEEDED", HttpStatus.BAD_REQUEST, "Study capacity exceeded."),
  ALREADY_STUDY_MEMBER("ALREADY_STUDY_MEMBER", HttpStatus.CONFLICT, "User is already a study member."),
  RECRUITMENT_NOT_OPEN("RECRUITMENT_NOT_OPEN", HttpStatus.BAD_REQUEST, "Recruitment is not open.");

  private final String code;
  private final HttpStatus status;
  private final String message;
}
