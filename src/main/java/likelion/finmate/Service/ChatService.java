package likelion.finmate.Service;

import likelion.finmate.Dto.ChatRequest;
import likelion.finmate.Dto.ChatResponse;
import likelion.finmate.Entity.User;
import likelion.finmate.Repository.UserRepository;
import likelion.finmate.External.GeminiClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * v0: LLM API + 로그인 사용자 기반 초개인화.
 * 프로필 필드는 User 엔티티에 없을 수 있으므로 기본값으로 처리한다.
 * (추후 별도 Profile 테이블이 생기면 교체)
 */
@Service
public class ChatService {

    private final UserRepository users;
    private final GeminiClient gemini;

    public ChatService(UserRepository users, GeminiClient gemini) {
        this.users = users;
        this.gemini = gemini;
        // 추후 필요하면 생성자에서 로깅 등 추가
    }

    // ⭐ 기존: handle(ChatRequest req)
    //    → 변경: 로그인한 사용자의 userId 를 함께 받음
    public ChatResponse handle(String userId, ChatRequest req) {

        // 1) 로그인한 사용자 조회 (없으면 기본 프로필로 처리)
        User u = users.findByUserId(userId).orElse(null);

        String nickname =
                (u != null && u.getNickname() != null && !u.getNickname().isBlank())
                        ? u.getNickname()
                        : "친구";

        // v0 기본 프로필 (엔티티에 아직 필드가 없다 가정)
        // 나중에 User 엔티티에 level, risk, interests 생기면 여기서 꺼내 쓰면 된다.
        String level = "beginner";          // beginner | intermediate
        String risk  = "saver";             // saver | neutral | challenger
        String interests = "[]";            // 추후 user_interest 테이블 기반으로 교체 예정

        // 2) 프롬프트 조립 (닉네임 + 레벨/성향 + 유저 질문)
        String prompt = PromptTemplate.build(
                nickname,
                level,
                risk,
                interests,
                req.message()
        );

        // 3) 모델 호출 (Gemini)
        String answer = gemini.generate(prompt, 0.7, 800);

        // 4) 형식 보정 + 안전 문구
        if (answer == null || answer.isBlank()) {
            // 한 번 더 보수적으로 재시도
            answer = gemini.generate(prompt, 0.2, 800);
        }
        if (!answer.endsWith("\n")) answer += "\n";
        answer += "\n— 본 답변은 일반 교육 목적이며, 투자 권유가 아니다.";

        Object debug = Boolean.TRUE.equals(req.trace())
                ? Map.of("promptPreview", prompt.substring(0, Math.min(800, prompt.length())))
                : null;

        return new ChatResponse(answer, level, risk, List.of(), debug);
    }
}
