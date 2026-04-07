package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class AlreadyStudyMemberException extends BusinessException {

  public AlreadyStudyMemberException() {
    super(StudyErrorCode.ALREADY_STUDY_MEMBER);
  }
}


