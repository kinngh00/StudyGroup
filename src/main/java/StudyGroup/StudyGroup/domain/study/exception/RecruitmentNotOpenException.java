package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class RecruitmentNotOpenException extends BusinessException {

  public RecruitmentNotOpenException() {
    super(StudyErrorCode.RECRUITMENT_NOT_OPEN);
  }
}


