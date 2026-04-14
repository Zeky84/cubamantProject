package com.company.cubamant.b_service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.cubamant.domain.RefreshToken;
import com.company.cubamant.domain.User;
import com.company.cubamant.ab_payload.RefreshTokenRequest;
import com.company.cubamant.repository.RefreshTokenRepository;
import com.company.cubamant.repository.UserRepository;

@Service
public class RefreshTokenService {

	@Value("${token.refreshExpiration}")
	private Long refreshTokenDuration; // in milliseconds

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

	// ===== CREATE NEW REFRESH TOKEN =====
	@Transactional
	public RefreshToken createRefreshToken(Long userId) {
		// Remove old tokens to prevent multiple valid tokens
		refreshTokenRepository.deleteByUserId(userId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(user);
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDuration));
		refreshToken.setRevoked(false);
		refreshToken.setReused(false);

		return refreshTokenRepository.save(refreshToken);
	}

	// ===== FIND REFRESH TOKEN BY TOKEN STRING =====
	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	// ===== VERIFY AND ROTATE =====
	@Transactional
	public RefreshToken verifyAndRotate(RefreshToken token) {
		// 1️⃣ Reuse detection
		if (token.isRevoked()) {
			handleReuseAttack(token);
			throw new RuntimeException("Detected refresh token reuse. Please sign in again.");
		}

		// 2️⃣ Expiration check
		if (token.getExpiryDate().isBefore(Instant.now())) {
			refreshTokenRepository.delete(token);
			throw new RuntimeException("Refresh token expired. Please sign in again.");
		}

		// 3️⃣ Rotation: revoke old token
		token.setRevoked(true);
		refreshTokenRepository.save(token);

		// 4️⃣ Create a new token for the user
		return createRefreshToken(token.getUser().getId());
	}

	// ===== CREATE NEW ACCESS TOKEN (CALLED FROM CONTROLLER) =====
	@Transactional
	public String createNewAccessToken(RefreshTokenRequest request) {
		RefreshToken rotatedToken = findByToken(request.refreshToken())
				.map(this::verifyAndRotate)
				.orElseThrow(() -> new RuntimeException("Refresh token not found"));

		// Generate new JWT for the user
		return jwtService.generateToken(rotatedToken.getUser());
	}

	// ===== HANDLE REUSE ATTACK =====
	@Transactional
	void handleReuseAttack(RefreshToken token) {
		token.setReused(true);
		refreshTokenRepository.save(token);

		Long userId = token.getUser().getId();
		// Revoke all tokens for this user
		refreshTokenRepository.deleteByUserId(userId);
	}

	// ===== DELETE ALL TOKENS FOR USER =====
	@Transactional
	public int deleteByUserId(Long userId) {
		return refreshTokenRepository.deleteByUserId(userId);
	}
}
