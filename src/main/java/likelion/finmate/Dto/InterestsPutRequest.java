package likelion.finmate.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class InterestsPutRequest {
    private List<String> interests;
}
