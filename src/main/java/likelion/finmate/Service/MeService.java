package likelion.finmate.Service;

import likelion.finmate.Dto.MePatchRequest;
import likelion.finmate.Dto.MeResponse;
import likelion.finmate.Dto.OptionalProfilePutRequest;
import likelion.finmate.Entity.OptionalProfile;
import likelion.finmate.Entity.User;
import likelion.finmate.Entity.UserInterest;
import likelion.finmate.Repository.OptionalProfileRepository;
import likelion.finmate.Repository.UserInterestRepository;
import likelion.finmate.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MeService {

    private final UserInterestRepository userInterestRepository;
    private final OptionalProfileRepository optionalProfileRepository;
    private final UserRepository userRepository;

    @Value("${app.upload-dir}")
    private String uploadBaseDir;

    // 내 정보 조회
    public MeResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<String> interests = userInterestRepository.findByUserId(userId)
                .stream()
                .map(UserInterest::getInterestType)
                .toList();

        OptionalProfile profile = optionalProfileRepository.findByUserId(userId).orElse(null);

        return new MeResponse(
                user.getId(),
                user.getNickname(),
                user.getInvestmentTendency() == null ? null : user.getInvestmentTendency().name(),
                user.getFinancialLevel() == null ? null : user.getFinancialLevel().name(),
                interests,
                profile == null ? null :
                        new MeResponse.OptionalProfileDto(
                                profile.getSpendingPattern(),
                                profile.getCashRatio(),
                                profile.getHasHouseholdLedger(),
                                profile.getRegionCode(),
                                profile.getProfileImageUrl()
                        )
        );
    }

    // 닉네임 / 투자성향 / 금융지식 수정(부분 갱신)
    public MeResponse patchMe(Long userId, MePatchRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (req.getNickname() != null) {
            user.setNickname(req.getNickname());
        }
        if (req.getRiskType() != null) {
            user.setInvestmentTendency(User.InvestmentTendency.valueOf(req.getRiskType()));
        }
        if (req.getKnowledgeLevel() != null) {
            user.setFinancialLevel(User.FinancialLevel.valueOf(req.getKnowledgeLevel()));
        }

        userRepository.save(user);
        return getMe(userId);
    }

    // 관심 키워드 (전체 교체)
    public void putInterests(Long userId, List<String> interests) {
        userInterestRepository.deleteByUserId(userId);

        List<UserInterest> list = interests.stream()
                .map(i -> new UserInterest(userId, i, i + " 관련 관심"))
                .toList();

        userInterestRepository.saveAll(list);
    }

    // 부가 정보 upsert
    public void putOptional(Long userId, OptionalProfilePutRequest req) {
        OptionalProfile profile = optionalProfileRepository.findByUserId(userId)
                .orElse(new OptionalProfile(userId));

        profile.update(
                req.getSpendingPattern(),
                req.getCashRatio(),
                req.getHasHouseholdLedger(),
                req.getRegionCode()
        );

        optionalProfileRepository.save(profile);
    }

    // 프로필 이미지 업로드
    public String updateProfileImage(Long userId, MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        OptionalProfile profile = optionalProfileRepository.findByUserId(userId)
                .orElse(new OptionalProfile(userId));

        try {
            Path profileDir = Paths.get(uploadBaseDir, "profile");
            Files.createDirectories(profileDir);

            String originalName = imageFile.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" +
                    (originalName != null ? originalName : "profile.img");

            Path destPath = profileDir.resolve(fileName);

            imageFile.transferTo(destPath);

            String url = "/uploads/profile/" + fileName;
            profile.setProfileImageUrl(url);
            optionalProfileRepository.save(profile);

            return url;
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
