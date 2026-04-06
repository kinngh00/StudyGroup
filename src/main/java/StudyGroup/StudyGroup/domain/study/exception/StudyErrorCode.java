package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyErrorCode implements ExceptionCode {
  STUDY_GROUP_NOT_FOUND("STUDY_GROUP_NOT_FOUND", HttpStatus.NOT_FOUND, "���͵� �׷��� ã�� �� �����ϴ�."),
  STUDY_ACCESS_DENIED("STUDY_ACCESS_DENIED", HttpStatus.FORBIDDEN, "���͵� �׷쿡 ������ ������ �����ϴ�."),
  STUDY_MEMBER_NOT_FOUND("STUDY_MEMBER_NOT_FOUND", HttpStatus.NOT_FOUND, "���͵� ����� ã�� �� �����ϴ�."),
  STUDY_SCHEDULE_NOT_FOUND("STUDY_SCHEDULE_NOT_FOUND", HttpStatus.NOT_FOUND, "���͵� ������ ã�� �� �����ϴ�."),
  STUDY_NOTICE_NOT_FOUND("STUDY_NOTICE_NOT_FOUND", HttpStatus.NOT_FOUND, "���͵� ������ ã�� �� �����ϴ�."),
  STUDY_REPORT_NOT_FOUND("STUDY_REPORT_NOT_FOUND", HttpStatus.NOT_FOUND, "���͵� �Ű�� ã�� �� �����ϴ�."),
  STUDY_REPORT_ALREADY_RESOLVED("STUDY_REPORT_ALREADY_RESOLVED", HttpStatus.BAD_REQUEST, "�̹� ó�� �Ϸ�� �Ű��Դϴ�."),
  STUDY_USER_BLOCKED("STUDY_USER_BLOCKED", HttpStatus.FORBIDDEN, "�ش� ���͵𿡼� Ȱ���� ���ѵ� ������Դϴ�."),
  STUDY_USER_BLACKLISTED("STUDY_USER_BLACKLISTED", HttpStatus.FORBIDDEN, "�ش� ���͵� �������Ʈ ������Դϴ�."),
  RECRUITMENT_POST_NOT_FOUND("RECRUITMENT_POST_NOT_FOUND", HttpStatus.NOT_FOUND, "�������� ã�� �� �����ϴ�."),
  STUDY_APPLICATION_NOT_FOUND("STUDY_APPLICATION_NOT_FOUND", HttpStatus.NOT_FOUND, "���� ��û ������ ã�� �� �����ϴ�."),
  STUDY_APPLICATION_DUPLICATED("STUDY_APPLICATION_DUPLICATED", HttpStatus.CONFLICT, "�̹� ���� ��û�� ���͵��Դϴ�."),
  STUDY_APPLICATION_NOT_PENDING("STUDY_APPLICATION_NOT_PENDING", HttpStatus.BAD_REQUEST, "������� ���� ��û�� ó���� �� �ֽ��ϴ�."),
  STUDY_CAPACITY_EXCEEDED("STUDY_CAPACITY_EXCEEDED", HttpStatus.BAD_REQUEST, "���͵� ������ ���� á���ϴ�."),
  ALREADY_STUDY_MEMBER("ALREADY_STUDY_MEMBER", HttpStatus.CONFLICT, "�̹� ���͵� ����Դϴ�."),
  RECRUITMENT_NOT_OPEN("RECRUITMENT_NOT_OPEN", HttpStatus.BAD_REQUEST, "�������� �ۿ����� ���� ��û�� �� �ֽ��ϴ�.");

  private final String code;
  private final HttpStatus status;
  private final String message;
}
