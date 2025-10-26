package likelion.finmate.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt는 안전한 해싱 알고리즘입니다.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. HTTP Basic 인증 비활성화
                .httpBasic(basic -> basic.disable())

                // 2. CSRF (Cross-Site Request Forgery) 공격 방어 기능 비활성화
                .csrf(csrf -> csrf.disable())

                // 3. CORS 설정 (기본 설정을 사용하거나 필요에 따라 상세 설정 추가)
                .cors(cors -> {})

                // 4. 세션 관리 정책 설정 (STATELESS: 세션 사용 안 함, JWT 등에 적합)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. URL 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/register", "/users/login", "/users/logout", "/users/withdraw").permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}