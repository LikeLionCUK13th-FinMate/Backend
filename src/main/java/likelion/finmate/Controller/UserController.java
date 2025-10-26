package likelion.finmate.Controller;

import jakarta.servlet.http.HttpSession;
import likelion.finmate.Dto.LoginRequestDto;
import likelion.finmate.Dto.UserRegistrationDto;
import likelion.finmate.Entity.User;
import likelion.finmate.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto dto) {
        try {
            userService.registerUser(dto);
            return new ResponseEntity<>("회원가입이 성공적으로 완료되었습니다.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request, HttpSession session) {
        try {
            User user = userService.loginUser(request.getUserId(), request.getPassword());

            // **[NOTE]** 실제로는 세션 대신 JWT 토큰을 클라이언트에게 발급해야 합니다.
            session.setAttribute("LOGIN_USER", user.getUserId());

            return ResponseEntity.ok("로그인 성공! Welcome, " + user.getNickname());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return ResponseEntity.ok("로그아웃되었습니다.");
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody LoginRequestDto request, HttpSession session) {
        // 세션에서 사용자 정보 확인 (실제로는 보안상 인증된 사용자만 허용)
        if (session.getAttribute("LOGIN_USER") == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            userService.deleteUser(request.getUserId(), request.getPassword());
            session.invalidate(); // 탈퇴 후 세션 무효화
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다. 그동안 이용해 주셔서 감사합니다.");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}