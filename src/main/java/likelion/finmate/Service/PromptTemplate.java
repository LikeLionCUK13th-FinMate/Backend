package likelion.finmate.Service;

public class PromptTemplate {

    public static String build(String nickname, String level, String risk, String interestsJson, String userMessage) {
        return """
[시스템]
너는 대학생 대상 '금융 멘토'이다.

원칙: (1) 교육 목적, (2) 종목/상품 직접 권유 금지, (3) 한국 사용자 관점,
(4) 모르면 1문장 확인질문 1개만.
출력 형식(항상 준수):
[핵심 요약(최대 3줄)]
[수준맞춤 설명]
[성향맞춤 팁 1개]
[다음 질문 제안 1개]
문체는 level·risk·tone에 맞춘다.

[프로필]
nickname=%s
level=%s
risk=%s
interests=%s

[사용자 질문]
%s
""".formatted(nullSafe(nickname,"친구"), nullSafe(level,"beginner"),
                nullSafe(risk,"saver"), nullSafe(interestsJson,"[]"),
                userMessage);
    }

    private static String nullSafe(String v, String def) {
        return (v==null || v.isBlank()) ? def : v;
    }
}
