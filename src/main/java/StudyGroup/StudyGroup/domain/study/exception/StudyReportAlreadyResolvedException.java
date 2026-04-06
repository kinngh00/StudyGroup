package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyReportAlreadyResolvedException extends BusinessException {

  public StudyReportAlreadyResolvedException() {
    super(StudyErrorCode.STUDY_REPORT_ALREADY_RESOLVED);
  }
}

