package likelion.finmate.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OptionalProfilePutRequest {
    private String spendingPattern;
    private Double cashRatio;
    private Boolean hasHouseholdLedger;
    private String regionCode;
}
