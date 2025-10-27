package likelion.finmate.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion.finmate.Entity.User;
import likelion.finmate.Repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository; // 사용자 정보 로드를 위해 Repository 사용

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request); // 1. HTTP 헤더에서 JWT 추출

            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                String userId = jwtTokenProvider.getUserIdFromToken(jwt); // 2. 토큰에서 사용자 ID 추출

                // 3. 사용자 ID로 DB에서 사용자 정보 로드 (User Entity 사용)
                User user = userRepository.findByUserIdAndIsDeletedFalse(userId).orElse(null);

                if (user != null) {
                    // 4. 인증 객체 생성
                    // JWT는 이미 검증되었으므로, 비밀번호 없이 인증 토큰 생성
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user.getUserId(), // Principal: 사용자 ID
                            null, // Credentials: 비밀번호는 null
                            null // Authorities: 권한 정보 (여기서는 단순 인증이므로 null)
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. Security Context에 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // 토큰 파싱 또는 사용자 로드 중 오류 발생 시 로깅
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 토큰 추출 (Bearer scheme)
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
