package com.company.cubamant.b_service;

import com.company.cubamant.domain.SetupToken;
import com.company.cubamant.domain.User;
import com.company.cubamant.repository.SetupTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class SetupService {

	private final SetupTokenRepository setupTokenRepository;

	public SetupService(SetupTokenRepository setupTokenRepository) {
		this.setupTokenRepository = setupTokenRepository;
	}

	public SetupToken createToken(User user) {
		String token = UUID.randomUUID().toString();

		SetupToken setupToken = new SetupToken(
				token,
				user,
				Instant.now().plusSeconds(60 * 60 * 24) // 24h
		);

		return setupTokenRepository.save(setupToken);
	}

	public Optional<SetupToken> validateToken(String token) {
		Optional<SetupToken> tokenOpt = setupTokenRepository.findByToken(token);

		if (tokenOpt.isEmpty()) return Optional.empty();

		SetupToken setupToken = tokenOpt.get();

		if (setupToken.getExpiration().isBefore(Instant.now())) {
			return Optional.empty();
		}

		return Optional.of(setupToken);
	}
}
