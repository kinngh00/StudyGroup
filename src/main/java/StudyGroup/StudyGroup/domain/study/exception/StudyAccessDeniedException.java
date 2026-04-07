package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyAccessDeniedException extends BusinessException {

  public StudyAccessDeniedException() {
    super(StudyErrorCode.STUDY_ACCESS_DENIED);
  }
}


