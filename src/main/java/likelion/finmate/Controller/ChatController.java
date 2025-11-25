package likelion.finmate.Controller;

import jakarta.validation.Valid;
import likelion.finmate.Dto.ChatRequest;
import likelion.finmate.Dto.ChatResponse;
import likelion.finmate.Service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chat;

    public ChatController(ChatService chat) {
        this.chat = chat;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            Authentication authentication,
            @Valid @RequestBody ChatRequest req
    ) {
        // JWT 필터가 만들어준 Authentication 에서 userId(subject) 꺼냄
        String userId = authentication.getName();   // ex) "testuser02"

        ChatResponse response = chat.handle(userId, req);
        return ResponseEntity.ok(response);
    }
}
