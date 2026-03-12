package com.company.cubamant.b_service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.company.cubamant.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.company.cubamant.domain.RefreshToken;
import com.company.cubamant.domain.User;
import com.company.cubamant.ab_payload.RefreshTokenRequest;
import com.company.cubamant.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {
	@Value("${token.refreshExpiration}")
	private Long refreshTokenDuration;

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final JwtService jwtService;

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
							   UserRepository userRepository,
							   JwtService jwtService) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.userRepository = userRepository;
		this.jwtService = jwtService;
	}

	@Transactional
	public RefreshToken createRefreshToken(Long userId) {
		// Delete old refresh tokens for this user
		refreshTokenRepository.deleteByUserId(userId);

		RefreshToken refreshToken = new RefreshToken();
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		refreshToken.setUser(user);
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDuration));
		refreshToken.setToken(UUID.randomUUID().toString());

		return refreshTokenRepository.save(refreshToken);
	}

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public Optional<RefreshToken> findByUserId(Long userId) {
		return refreshTokenRepository.findByUserId(userId);
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.deleteById(token.getId());
			throw new RuntimeException("Refresh token expired. Please sign in again.");
		}
		return token;
	}

	public String createNewAccessToken(RefreshTokenRequest request) {
		RefreshToken refreshToken = findByToken(request.refreshToken())
				.map(this::verifyExpiration)
				.orElseThrow(() -> new RuntimeException("Refresh token not found"));

		User user = refreshToken.getUser();
		return jwtService.generateToken(user);
	}

	@Transactional
	public void deleteByUserId(Long id) {
		refreshTokenRepository.deleteByUserId(id);
	}
}
