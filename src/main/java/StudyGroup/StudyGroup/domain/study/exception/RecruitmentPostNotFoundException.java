package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class RecruitmentPostNotFoundException extends BusinessException {

  public RecruitmentPostNotFoundException() {
    super(StudyErrorCode.RECRUITMENT_POST_NOT_FOUND);
  }
}


