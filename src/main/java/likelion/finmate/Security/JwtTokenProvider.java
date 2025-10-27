package likelion.finmate.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간 (ms 단위)

    // application.properties에서 설정한 키를 주입받아 SecretKey 객체 생성
    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(userId) // 토큰 주체 (사용자 ID)
                .issuedAt(now) // 발급 시간
                .expiration(expiryDate) // 만료 시간
                .signWith(key, Jwts.SIG.HS256) // HS256 알고리즘과 비밀 키로 서명
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key) // 비밀 키로 서명 검증
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // 잘못된 JWT 서명
            System.err.println("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            // 만료된 JWT 토큰
            System.err.println("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰
            System.err.println("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 잘못됨
            System.err.println("JWT claims string is empty.");
        }
        return false;
    }
}