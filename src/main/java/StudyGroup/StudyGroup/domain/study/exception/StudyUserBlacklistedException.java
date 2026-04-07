package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyUserBlacklistedException extends BusinessException {

  public StudyUserBlacklistedException() {
    super(StudyErrorCode.STUDY_USER_BLACKLISTED);
  }
}
