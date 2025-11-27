package likelion.finmate.Controller;

import likelion.finmate.Dto.OptionalProfilePutRequest;
import likelion.finmate.Entity.User;
import likelion.finmate.Repository.UserRepository;
import likelion.finmate.Service.MeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final MeService meService;
    private final UserRepository userRepository;

    private Long getCurrentUserPk() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        String loginId = (String) authentication.getPrincipal();
        User user = userRepository.findByUserIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return user.getId();
    }

    @PostMapping("/optional")
    public ResponseEntity<Map<String, String>> postOptional(@RequestBody OptionalProfilePutRequest req) {
        Long userId = getCurrentUserPk();
        meService.putOptional(userId, req);
        return ResponseEntity.ok(Map.of("message", "optional profile upserted"));
    }
}
