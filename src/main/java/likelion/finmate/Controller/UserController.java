package likelion.finmate.Controller;

import jakarta.servlet.http.HttpSession;
import likelion.finmate.Dto.CommonResponse;
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
    public ResponseEntity<CommonResponse<Void>> register(@RequestBody UserRegistrationDto dto) {
        try {
            userService.registerUser(dto);
            // HTTP 201 Created 반환
            return new ResponseEntity<>(
                    CommonResponse.success("회원가입이 성공적으로 완료되었습니다.", HttpStatus.CREATED),
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException e) {
            // HTTP 400 Bad Request 반환
            return new ResponseEntity<>(
                    CommonResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<String>> login(@RequestBody LoginRequestDto request, HttpSession session) {
        try {
            User user = userService.loginUser(request.getUserId(), request.getPassword());
            session.setAttribute("LOGIN_USER", user.getUserId());
            String token = "YOUR_GENERATED_JWT_TOKEN_HERE";

            return ResponseEntity.ok(
                    CommonResponse.success("로그인 성공! 환영합니다, " + user.getNickname(), token, HttpStatus.OK)
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    CommonResponse.<String>error(e.getMessage(), HttpStatus.UNAUTHORIZED),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(HttpSession session) {

        if (session != null) {
            session.invalidate(); // 세션 무효화
        }

        return ResponseEntity.ok(
                CommonResponse.success("로그아웃되었습니다.", HttpStatus.OK)
        );
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<CommonResponse<Void>> withdraw(@RequestBody LoginRequestDto request, HttpSession session) {

        String loggedInUserId = (String) session.getAttribute("LOGIN_USER");

        if (loggedInUserId == null || !loggedInUserId.equals(request.getUserId())) {
            return new ResponseEntity<>(
                    CommonResponse.error("인증되지 않은 사용자이거나, 탈퇴하려는 사용자가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
                    HttpStatus.UNAUTHORIZED
            );
        }

        try {
            userService.deleteUser(request.getUserId(), request.getPassword());

            if (session != null) {
                session.invalidate(); // 탈퇴 후 세션 무효화
            }

            return ResponseEntity.ok(
                    CommonResponse.success("회원 탈퇴가 완료되었습니다.", HttpStatus.OK)
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    CommonResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}