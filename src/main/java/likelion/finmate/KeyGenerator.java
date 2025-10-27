package likelion.finmate;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 32바이트 = 256비트
        secureRandom.nextBytes(keyBytes);
        String secretKey = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("--- 생성된 JWT 비밀 키 (복사하여 환경 변수에 사용) ---");
        System.out.println(secretKey);
        System.out.println("-----------------------------------------------------");
    }
}