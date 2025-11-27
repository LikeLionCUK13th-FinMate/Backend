package likelion.finmate.Dto;

import java.util.List;

public record ChatResponse(
        String answer,
        String level,
        String risk,
        List<String> sources,
        Object debug
) { }
