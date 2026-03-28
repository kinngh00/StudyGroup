package SudyGroup.StudyGroup.global.exception;

public record ExceptionResponseDto (
  int status,
  String code,
  String message
){
  public ExceptionResponseDto(CustomException e) {

    this(
        e.getErrorCode().getStatusValue(),
        e.getErrorCode().getCode(),
        e.getErrorCode().getMessage()
    );
  }
}
