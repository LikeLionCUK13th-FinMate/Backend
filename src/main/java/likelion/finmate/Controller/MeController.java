package likelion.finmate.Controller;

import likelion.finmate.Dto.InterestsPutRequest;
import likelion.finmate.Dto.MePatchRequest;
import likelion.finmate.Dto.MeResponse;
import likelion.finmate.Dto.OptionalProfilePutRequest;
import likelion.finmate.Entity.User;
import likelion.finmate.Repository.UserRepository;
import likelion.finmate.Service.MeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/me")
public class MeController {

    private final MeService meService;
    private final UserRepository userRepository;

    private Long getCurrentUserPk() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        String loginId = (String) authentication.getPrincipal(); // JwtAuthenticationFilter에서 넣은 userId
        User user = userRepository.findByUserIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return user.getId();
    }

    // 내 정보 조회
    @GetMapping
    public ResponseEntity<MeResponse> getMe() {
        Long userId = getCurrentUserPk();
        return ResponseEntity.ok(meService.getMe(userId));
    }

    // 통합 PATCH (nickname, riskType, knowledgeLevel)
    @PatchMapping
    public ResponseEntity<MeResponse> patchMe(@RequestBody MePatchRequest req) {
        Long userId = getCurrentUserPk();
        return ResponseEntity.ok(meService.patchMe(userId, req));
    }

    // 닉네임만 수정
    @PatchMapping("/nickname")
    public ResponseEntity<MeResponse> updateNickname(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserPk();
        MePatchRequest req = new MePatchRequest();
        req.setNickname(body.get("nickname"));
        return ResponseEntity.ok(meService.patchMe(userId, req));
    }

    // 투자 성향 수정
    @PatchMapping("/risk-profile")
    public ResponseEntity<Map<String, Object>> updateRisk(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserPk();
        String riskProfile = body.get("risk_profile");

        MePatchRequest req = new MePatchRequest();
        req.setRiskType(riskProfile);
        meService.patchMe(userId, req);

        return new ResponseEntity<>(
                Map.of(
                        "message", "risk profile updated",
                        "risk_profile", riskProfile
                ),
                HttpStatus.OK
        );
    }

    // 금융 지식 수준 수정
    @PatchMapping("/knowledge-level")
    public ResponseEntity<Map<String, Object>> updateKnowledge(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserPk();
        String knowledge = body.get("financial_knowledge");

        MePatchRequest req = new MePatchRequest();
        req.setKnowledgeLevel(knowledge);
        meService.patchMe(userId, req);

        return new ResponseEntity<>(
                Map.of(
                        "message", "knowledge level updated",
                        "financial_knowledge", knowledge
                ),
                HttpStatus.OK
        );
    }

    // 관심 키워드 전체 교체
    @PutMapping("/interests")
    public ResponseEntity<Map<String, String>> putInterests(@RequestBody InterestsPutRequest req) {
        Long userId = getCurrentUserPk();
        meService.putInterests(userId, req.getInterests());
        return ResponseEntity.ok(Map.of("message", "interests replaced"));
    }

    // 선택 정보 upsert (PUT 버전)
    @PutMapping("/optional-profile")
    public ResponseEntity<Map<String, String>> putOptional(@RequestBody OptionalProfilePutRequest req) {
        Long userId = getCurrentUserPk();
        meService.putOptional(userId, req);
        return ResponseEntity.ok(Map.of("message", "optional profile upserted"));
    }

    // 프로필 이미지 업로드
    @PatchMapping("/profile-image")
    public ResponseEntity<Map<String, String>> updateProfileImage(@RequestPart("image") MultipartFile imageFile) {
        Long userId = getCurrentUserPk();
        String url = meService.updateProfileImage(userId, imageFile);
        return ResponseEntity.ok(
                Map.of(
                        "message", "profile image updated",
                        "imageUrl", url
                )
        );
    }
}
