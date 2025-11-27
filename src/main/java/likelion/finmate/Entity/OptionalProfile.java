package likelion.finmate.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "optional_profile")
public class OptionalProfile {

    @Id
    private Long userId; // User와 1:1, PK 공유

    @Column(length = 50)
    private String spendingPattern;

    private Double cashRatio;

    private Boolean hasHouseholdLedger;

    @Column(length = 30)
    private String regionCode;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    public OptionalProfile(Long userId) {
        this.userId = userId;
    }

    public void update(String spendingPattern, Double cashRatio,
                       Boolean hasHouseholdLedger, String regionCode) {
        this.spendingPattern = spendingPattern;
        this.cashRatio = cashRatio;
        this.hasHouseholdLedger = hasHouseholdLedger;
        this.regionCode = regionCode;
    }
}
