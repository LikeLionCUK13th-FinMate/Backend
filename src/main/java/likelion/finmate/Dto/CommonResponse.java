package likelion.finmate.Dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder // 빌더 패턴으로 쉽게 객체 생성
public class CommonResponse<T> {
    private final boolean success;
    private final int code;
    private final String message;
    private final T data;

    // 성공 응답 (데이터 포함)
    public static <T> CommonResponse<T> success(String message, T data, HttpStatus status) {
        return CommonResponse.<T>builder()
                .success(true)
                .code(status.value())
                .message(message)
                .data(data)
                .build();
    }

    // 성공 응답 (데이터 없음)
    public static CommonResponse<Void> success(String message, HttpStatus status) {
        return CommonResponse.<Void>builder()
                .success(true)
                .code(status.value())
                .message(message)
                .data(null)
                .build();
    }

    // 실패/에러 응답
    public static <T> CommonResponse<T> error(String message, HttpStatus status) {
        return CommonResponse.<T>builder() // T 타입으로 빌더 생성
                .success(false)
                .code(status.value())
                .message(message)
                .data(null) // 데이터는 null
                .build();
    }
}