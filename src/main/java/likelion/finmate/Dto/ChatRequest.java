package likelion.finmate.Dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(

        @NotBlank(message = "메시지는 공백일 수 없습니다.")
        String message,

        // trace는 옵션이니까 검증 없음
        Boolean trace
) {}
