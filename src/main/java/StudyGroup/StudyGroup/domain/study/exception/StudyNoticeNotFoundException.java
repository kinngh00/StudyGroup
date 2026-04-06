package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyNoticeNotFoundException extends BusinessException {

  public StudyNoticeNotFoundException() {
    super(StudyErrorCode.STUDY_NOTICE_NOT_FOUND);
  }
}

