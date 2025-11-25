package likelion.finmate.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MePatchRequest {
    private String nickname;       // user.nickname
    private String riskType;       // User.InvestmentTendency 이름
    private String knowledgeLevel; // User.FinancialLevel 이름
}
