package likelion.finmate.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserRegistrationDto {
    @NotBlank
    private String userId;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;

    private Integer age;
    private String gender; // MALE, FEMALE
    private String financialLevel; // BEGINNER, INTERMEDIATE, ADVANCED
    private List<String> interestFields; // 관심 분야
    private String job;
    private String monthlyIncomeRange;
    private String financialGoal; // SHORT_TERM, MID_TERM, LONG_TERM
    private String investmentTendency; // SAFE, NEUTRAL, AGGRESSIVE
    private List<String> currentMainConcerns; // 현재 주요 관심사
}