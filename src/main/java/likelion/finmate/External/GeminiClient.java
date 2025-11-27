package likelion.finmate.External;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GeminiClient {

    // ⭐ Gemini 2.0 이상 모델만 사용 가능!
    private static final String MODEL = "gemini-2.5-flash";
    // 또는 안정 버전: "gemini-2.5-flash"


    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public GeminiClient(@Value("${gemini.api.key}") String apiKey) {
        String base = "https://generativelanguage.googleapis.com/v1beta/models/"
                + MODEL + ":generateContent?key=" + apiKey;

        this.webClient = WebClient.builder()
                .baseUrl(base)
                .build();
    }

    public String generate(String prompt, double temperature, int maxTokens) {
        // ⭐ maxTokens가 너무 작으면 최소 2048로 보장
        int actualMaxTokens = Math.max(maxTokens, 2048);

        String body = """
    {
      "contents": [{
        "role": "user",
        "parts": [{"text": %s}]
      }],
      "generationConfig": {
        "temperature": %s,
        "maxOutputTokens": %s
      }
    }
    """.formatted(jsonEscape(prompt), temperature, actualMaxTokens);

        String resp = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("{\"error\":\"" + e.getMessage() + "\"}"))
                .block();

        return extractText(resp);
    }

    private String extractText(String resp) {
        try {
            // ⭐ 1. 전체 응답 출력 (디버깅용)
            System.out.println("=== Gemini Full Response ===");
            System.out.println(resp);
            System.out.println("============================");

            JsonNode root = mapper.readTree(resp);

            // ⭐ 2. candidates 있는지 확인
            JsonNode candidates = root.get("candidates");
            System.out.println("candidates exists: " + (candidates != null));
            System.out.println("candidates: " + candidates);

            JsonNode parts = root.at("/candidates/0/content/parts");

            // ⭐ 3. parts 있는지 확인
            System.out.println("parts exists: " + (parts != null));
            System.out.println("parts isArray: " + parts.isArray());
            System.out.println("parts: " + parts);

            if (parts.isArray()) {
                StringBuilder sb = new StringBuilder();
                for (JsonNode p : parts) {
                    JsonNode t = p.get("text");
                    if (t != null) sb.append(t.asText());
                }
                String result = sb.toString();
                System.out.println("Extracted text: " + result);
                return result;
            }

            JsonNode err = root.get("error");
            if (err != null) return "(gemini error) " + err.toString();

            return "(no text)";
        } catch (Exception e) {
            System.out.println("Parse exception: " + e.getMessage());
            e.printStackTrace();
            return "(parse error) " + e.getMessage();
        }
    }
    private static String jsonEscape(String s) {
        return "\"" + s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                + "\"";
    }
}