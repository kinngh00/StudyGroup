package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyMemberNotFoundException extends BusinessException {

  public StudyMemberNotFoundException() {
    super(StudyErrorCode.STUDY_MEMBER_NOT_FOUND);
  }
}


