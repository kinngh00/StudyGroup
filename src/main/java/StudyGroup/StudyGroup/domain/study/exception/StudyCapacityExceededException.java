package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyCapacityExceededException extends BusinessException {

  public StudyCapacityExceededException() {
    super(StudyErrorCode.STUDY_CAPACITY_EXCEEDED);
  }
}


