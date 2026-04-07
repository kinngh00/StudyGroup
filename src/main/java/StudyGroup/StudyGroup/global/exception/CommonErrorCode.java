package StudyGroup.StudyGroup.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ExceptionCode {
  INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST, "?섎せ???붿껌?낅땲??"),
  INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "?쒕쾭 ?대? ?ㅻ쪟媛 諛쒖깮?덉뒿?덈떎.");

  private final String code;
  private final HttpStatus status;
  private final String message;
}
