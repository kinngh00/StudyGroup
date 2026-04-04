package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ExceptionCode {
  DUPLICATE_EMAIL("DUPLICATE_EMAIL", HttpStatus.CONFLICT, "?대? ?ъ슜 以묒씤 ?대찓?쇱엯?덈떎."),
  INVALID_CREDENTIALS("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "?대찓???먮뒗 鍮꾨?踰덊샇媛 ?щ컮瑜댁? ?딆뒿?덈떎."),
  INVALID_GOOGLE_ID_TOKEN("INVALID_GOOGLE_ID_TOKEN", HttpStatus.UNAUTHORIZED, "Google id token???щ컮瑜댁? ?딆뒿?덈떎.");

  private final String code;
  private final HttpStatus status;
  private final String message;
}

