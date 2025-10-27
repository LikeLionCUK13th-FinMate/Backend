package likelion.finmate.Controller;

import likelion.finmate.Dto.CommonResponse;
import likelion.finmate.Dto.LoginRequestDto;
import likelion.finmate.Dto.UserRegistrationDto;
import likelion.finmate.Entity.User;
import likelion.finmate.Security.JwtTokenProvider;
import likelion.finmate.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

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
    public ResponseEntity<CommonResponse<String>> login(@RequestBody LoginRequestDto request) {
        try {
            User user = userService.loginUser(request.getUserId(), request.getPassword());
            String token = jwtTokenProvider.createToken(user.getUserId());

            return ResponseEntity.ok(
                    CommonResponse.success("로그인 성공! 환영합니다, " + user.getNickname(), token, HttpStatus.OK)
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    CommonResponse.<String>error(e.getMessage(), HttpStatus.UNAUTHORIZED), // 타입 명시
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout() {
        return ResponseEntity.ok(
                CommonResponse.success("로그아웃되었습니다. 클라이언트의 토큰을 삭제하세요.", HttpStatus.OK)
        );
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<CommonResponse<Void>> withdraw(@RequestBody LoginRequestDto request) {

        // 💡 Security Context에서 현재 인증된 사용자 ID를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(
                    CommonResponse.error("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
                    HttpStatus.UNAUTHORIZED
            );
        }

        String loggedInUserId = (String) authentication.getPrincipal(); // JwtAuthenticationFilter에서 저장한 userId

        // 요청된 아이디와 로그인된 아이디가 일치하는지 확인
        if (!loggedInUserId.equals(request.getUserId())) {
            return new ResponseEntity<>(
                    CommonResponse.error("탈퇴하려는 사용자가 현재 로그인된 사용자와 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
                    HttpStatus.UNAUTHORIZED
            );
        }

        try {
            userService.deleteUser(request.getUserId(), request.getPassword());

            // 탈퇴 성공 후, Security Context도 클리어
            SecurityContextHolder.clearContext();

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