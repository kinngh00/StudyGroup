package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyErrorCode implements ExceptionCode {
  STUDY_GROUP_NOT_FOUND("STUDY_GROUP_NOT_FOUND", HttpStatus.NOT_FOUND, "?ㅽ꽣??洹몃９??李얠쓣 ???놁뒿?덈떎."),
  STUDY_ACCESS_DENIED("STUDY_ACCESS_DENIED", HttpStatus.FORBIDDEN, "?ㅽ꽣???묎렐 沅뚰븳???놁뒿?덈떎."),
  STUDY_MEMBER_NOT_FOUND("STUDY_MEMBER_NOT_FOUND", HttpStatus.NOT_FOUND, "?ㅽ꽣??硫ㅻ쾭瑜?李얠쓣 ???놁뒿?덈떎."),
  STUDY_SCHEDULE_NOT_FOUND("STUDY_SCHEDULE_NOT_FOUND", HttpStatus.NOT_FOUND, "?ㅽ꽣???쇱젙??李얠쓣 ???놁뒿?덈떎."),
  STUDY_NOTICE_NOT_FOUND("STUDY_NOTICE_NOT_FOUND", HttpStatus.NOT_FOUND, "?ㅽ꽣??怨듭?瑜?李얠쓣 ???놁뒿?덈떎."),
  STUDY_REPORT_NOT_FOUND("STUDY_REPORT_NOT_FOUND", HttpStatus.NOT_FOUND, "?좉퀬 ?댁뿭??李얠쓣 ???놁뒿?덈떎."),
  STUDY_REPORT_ALREADY_RESOLVED("STUDY_REPORT_ALREADY_RESOLVED", HttpStatus.BAD_REQUEST, "?대? 泥섎━???좉퀬?낅땲??"),
  STUDY_USER_BLOCKED("STUDY_USER_BLOCKED", HttpStatus.FORBIDDEN, "?대떦 ?ㅽ꽣?붿뿉???쒕룞???쒗븳???ъ슜?먯엯?덈떎."),
  STUDY_USER_BLACKLISTED("STUDY_USER_BLACKLISTED", HttpStatus.FORBIDDEN, "?대떦 ?ㅽ꽣??釉붾옓由ъ뒪???ъ슜?먯엯?덈떎."),
  RECRUITMENT_POST_NOT_FOUND("RECRUITMENT_POST_NOT_FOUND", HttpStatus.NOT_FOUND, "紐⑥쭛湲??李얠쓣 ???놁뒿?덈떎."),
  STUDY_APPLICATION_NOT_FOUND("STUDY_APPLICATION_NOT_FOUND", HttpStatus.NOT_FOUND, "媛???좎껌 ?뺣낫瑜?李얠쓣 ???놁뒿?덈떎."),
  STUDY_APPLICATION_DUPLICATED("STUDY_APPLICATION_DUPLICATED", HttpStatus.CONFLICT, "?대? 媛???좎껌???ъ슜?먯엯?덈떎."),
  STUDY_APPLICATION_NOT_PENDING("STUDY_APPLICATION_NOT_PENDING", HttpStatus.BAD_REQUEST, "?湲??곹깭???좎껌留?泥섎━?????덉뒿?덈떎."),
  STUDY_CAPACITY_EXCEEDED("STUDY_CAPACITY_EXCEEDED", HttpStatus.BAD_REQUEST, "?ㅽ꽣???뺤썝??媛??李쇱뒿?덈떎."),
  ALREADY_STUDY_MEMBER("ALREADY_STUDY_MEMBER", HttpStatus.CONFLICT, "?대? ?ㅽ꽣??硫ㅻ쾭?낅땲??"),
  RECRUITMENT_NOT_OPEN("RECRUITMENT_NOT_OPEN", HttpStatus.BAD_REQUEST, "紐⑥쭛 以묒씤 ?곹깭?먯꽌留??좎껌?????덉뒿?덈떎.");

  private final String code;
  private final HttpStatus status;
  private final String message;
}
