package likelion.finmate.Repository;

import likelion.finmate.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 회원가입 시 아이디 중복 확인
    Optional<User> findByUserId(String userId);

    // 로그인 및 탈퇴 확인 시 사용
    Optional<User> findByUserIdAndIsDeletedFalse(String userId);

    // 닉네임 중복 확인
    boolean existsByNickname(String nickname);
}