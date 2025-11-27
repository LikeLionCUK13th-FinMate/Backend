package likelion.finmate.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_interest")
public class UserInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "interest_type", nullable = false, length = 50)
    private String interestType;

    @Column(name = "`value`", length = 100)
    private String value;

    public UserInterest(Long userId, String interestType, String value) {
        this.userId = userId;
        this.interestType = interestType;
        this.value = value;
    }
}
