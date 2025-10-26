package likelion.finmate.Service;

import likelion.finmate.Dto.UserRegistrationDto;
import likelion.finmate.Entity.User;
import likelion.finmate.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 주입
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserRegistrationDto dto) {
        // 1. 아이디 중복 검사
        if (userRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        // 2. 닉네임 중복 검사
        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 4. User 객체 생성
        User user = new User(
                dto.getUserId(),
                encodedPassword, // 암호화된 비밀번호 저장
                dto.getNickname(),
                dto.getAge(),
                User.Gender.valueOf(dto.getGender()),
                User.FinancialLevel.valueOf(dto.getFinancialLevel()),
                dto.getJob(),
                dto.getMonthlyIncomeRange(),
                User.FinancialGoal.valueOf(dto.getFinancialGoal()),
                User.InvestmentTendency.valueOf(dto.getInvestmentTendency())
                // 관심 분야/관심사는 user_interest 테이블에 별도 저장 로직 필요 (여기서는 생략)
        );

        // 5. DB 저장
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User loginUser(String userId, String password) {
        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호 비교 (암호화된 비밀번호와 비교)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 시 User 객체 반환 (세션/JWT 생성에 사용)
        return user;
    }

    @Transactional
    public void deleteUser(String userId, String password) {
        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않아 탈퇴할 수 없습니다.");
        }

        // is_deleted 필드를 true로 변경 (Soft Delete)
        user.setDeleted(true);
        // user.setUpdatedAt(LocalDateTime.now()); // @Entity에 @UpdateTimestamp가 있다면 자동 처리됨
        userRepository.save(user);

        // 연관된 관심사 정보도 제거 (ON DELETE CASCADE 설정 시 DB에서 자동 제거됨)
    }
}