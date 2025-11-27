package likelion.finmate.Repository;

import likelion.finmate.Entity.OptionalProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OptionalProfileRepository extends JpaRepository<OptionalProfile, Long> {
    Optional<OptionalProfile> findByUserId(Long userId);
}
