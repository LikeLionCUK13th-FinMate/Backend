package likelion.finmate.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false, length = 50)
    private String userId; // 로그인 아이디

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(unique = true, nullable = false, length = 50)
    private String nickname;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender; // ENUM: MALE, FEMALE

    @Enumerated(EnumType.STRING)
    private FinancialLevel financialLevel; // ENUM: BEGINNER, INTERMEDIATE, ADVANCED

    private String job;

    @Column(name = "monthly_income_range")
    private String monthlyIncomeRange;

    @Enumerated(EnumType.STRING)
    private FinancialGoal financialGoal; // ENUM: SHORT_TERM, MID_TERM, LONG_TERM

    @Enumerated(EnumType.STRING)
    private InvestmentTendency investmentTendency; // ENUM: SAFE, NEUTRAL, AGGRESSIVE

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum Gender { MALE, FEMALE }
    public enum FinancialLevel { BEGINNER, INTERMEDIATE, ADVANCED }
    public enum FinancialGoal { SHORT_TERM, MID_TERM, LONG_TERM }
    public enum InvestmentTendency { SAFE, NEUTRAL, AGGRESSIVE }

    // 편의상 등록용 생성자 (서비스 로직에서 사용)
    public User(String userId, String password, String nickname, Integer age, Gender gender, FinancialLevel financialLevel, String job, String monthlyIncomeRange, FinancialGoal financialGoal, InvestmentTendency investmentTendency) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.financialLevel = financialLevel;
        this.job = job;
        this.monthlyIncomeRange = monthlyIncomeRange;
        this.financialGoal = financialGoal;
        this.investmentTendency = investmentTendency;
    }
}