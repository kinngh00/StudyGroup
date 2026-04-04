package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyErrorCode implements ExceptionCode {
  STUDY_GROUP_NOT_FOUND("STUDY_GROUP_NOT_FOUND", HttpStatus.NOT_FOUND, "?ㅽ꽣??洹몃９??李얠쓣 ???놁뒿?덈떎."),
  STUDY_ACCESS_DENIED("STUDY_ACCESS_DENIED", HttpStatus.FORBIDDEN, "?ㅽ꽣??洹몃９???묎렐??沅뚰븳???놁뒿?덈떎."),
  STUDY_MEMBER_NOT_FOUND("STUDY_MEMBER_NOT_FOUND", HttpStatus.NOT_FOUND, "?ㅽ꽣??硫ㅻ쾭瑜?李얠쓣 ???놁뒿?덈떎."),
  RECRUITMENT_POST_NOT_FOUND("RECRUITMENT_POST_NOT_FOUND", HttpStatus.NOT_FOUND, "紐⑥쭛湲??李얠쓣 ???놁뒿?덈떎."),
  STUDY_APPLICATION_NOT_FOUND("STUDY_APPLICATION_NOT_FOUND", HttpStatus.NOT_FOUND, "媛???좎껌 ?뺣낫瑜?李얠쓣 ???놁뒿?덈떎."),
  STUDY_APPLICATION_DUPLICATED("STUDY_APPLICATION_DUPLICATED", HttpStatus.CONFLICT, "?대? 媛???좎껌???ㅽ꽣?붿엯?덈떎."),
  STUDY_APPLICATION_NOT_PENDING("STUDY_APPLICATION_NOT_PENDING", HttpStatus.BAD_REQUEST, "?湲곗쨷??媛???좎껌留?泥섎━?????덉뒿?덈떎."),
  STUDY_CAPACITY_EXCEEDED("STUDY_CAPACITY_EXCEEDED", HttpStatus.BAD_REQUEST, "?ㅽ꽣???뺤썝??媛??李쇱뒿?덈떎."),
  ALREADY_STUDY_MEMBER("ALREADY_STUDY_MEMBER", HttpStatus.CONFLICT, "?대? ?ㅽ꽣??硫ㅻ쾭?낅땲??"),
  RECRUITMENT_NOT_OPEN("RECRUITMENT_NOT_OPEN", HttpStatus.BAD_REQUEST, "紐⑥쭛???대젮?덈뒗 湲?먯꽌留?媛???좎껌?????덉뒿?덈떎.");

  private final String code;
  private final HttpStatus status;
  private final String message;
}

