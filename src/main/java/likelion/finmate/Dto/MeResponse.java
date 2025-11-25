package likelion.finmate.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MeResponse {
    private Long userId;
    private String nickname;
    private String riskType;        // = User.InvestmentTendency.name()
    private String knowledgeLevel;  // = User.FinancialLevel.name()
    private List<String> interests;
    private OptionalProfileDto optionalProfile;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionalProfileDto {
        private String spendingPattern;
        private Double cashRatio;
        private Boolean hasHouseholdLedger;
        private String regionCode;
        private String profileImageUrl;
    }
}
