package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyScheduleNotFoundException extends BusinessException {

  public StudyScheduleNotFoundException() {
    super(StudyErrorCode.STUDY_SCHEDULE_NOT_FOUND);
  }
}
