package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyReportNotFoundException extends BusinessException {

  public StudyReportNotFoundException() {
    super(StudyErrorCode.STUDY_REPORT_NOT_FOUND);
  }
}

