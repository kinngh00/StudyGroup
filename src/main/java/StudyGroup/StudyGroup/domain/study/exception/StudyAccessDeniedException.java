package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;
import StudyGroup.StudyGroup.global.exception.ErrorCode;

public class StudyAccessDeniedException extends BusinessException {

  public StudyAccessDeniedException() {
    super(ErrorCode.STUDY_ACCESS_DENIED);
  }
}
