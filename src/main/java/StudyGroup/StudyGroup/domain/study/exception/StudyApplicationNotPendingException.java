package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyApplicationNotPendingException extends BusinessException {

  public StudyApplicationNotPendingException() {
    super(StudyErrorCode.STUDY_APPLICATION_NOT_PENDING);
  }
}


