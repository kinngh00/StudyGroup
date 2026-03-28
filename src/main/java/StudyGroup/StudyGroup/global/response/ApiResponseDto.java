package StudyGroup.StudyGroup.global.response;

public record ApiResponseDto<T>(
    int status,
    String code,
    String message,
    T data
) {
  public static <T> ApiResponseDto<T> success(String message, T data) {
    return new ApiResponseDto<>(200, "SUCCESS", message, data);
  }

  public static <T> ApiResponseDto<T> success(int status, String message, T data) {
    return new ApiResponseDto<>(status, "SUCCESS", message, data);
  }

  public static ApiResponseDto<Void> error(int status, String code, String message) {
    return new ApiResponseDto<>(status, code, message, null);
  }
}

