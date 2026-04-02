package com.company.cubamant.repository;

import com.company.cubamant.domain.SetupToken;
import com.company.cubamant.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SetupTokenRepository extends JpaRepository<SetupToken, Long> {
	Optional<SetupToken> findByToken(String token);
	Optional<SetupToken> findByTokenAndUsedFalse(String token);

	Optional<SetupToken> findByUser(User user);

	Optional<SetupToken> findByUserId(Long userId);
}
