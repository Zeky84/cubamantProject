package com.company.cubamant.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.cubamant.domain.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);
	Optional<RefreshToken> findByUserId(Long userId);
	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.user.id = :userId")
	int deleteByUserId(@Param("userId") Long userId);//GPT recomends me to return int instead of void, because it will return the number of deleted tokens(rows), which can be useful for debugging and logging purposes.
	int deleteAllByUserId(Long userId);// alternative method name for deleteByUserId
}
