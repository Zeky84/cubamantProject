package com.company.cubamant.b_service;

import com.company.cubamant.domain.*;
import com.company.cubamant.repository.SetupTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
public class SetupService {
	@Value("${app.base-url}")
	private String baseUrl;

	private final SetupTokenRepository setupTokenRepository;
	private final UserService userService;
	private final EmailService emailService;
	private final AuthorityService authorityService;

	private final PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(SetupService.class);

	public SetupService(SetupTokenRepository setupTokenRepository, UserService userService, EmailService emailService,
						AuthorityService authorityService, PasswordEncoder passwordEncoder) {
		this.setupTokenRepository = setupTokenRepository;
		this.userService = userService;
		this.emailService = emailService;
		this.authorityService = authorityService;
		this.passwordEncoder = passwordEncoder;
	}

	public SetupToken createToken(User user) {

		Optional<SetupToken> existingTokenOpt = setupTokenRepository.findByUser(user);

		if (existingTokenOpt.isPresent()) {
			SetupToken existing = existingTokenOpt.get();

			existing.setToken(UUID.randomUUID().toString());
			existing.setExpiration(Instant.now().plusSeconds(60 * 60 * 24));
			existing.setUsed(false);

			return setupTokenRepository.save(existing);
		}

		SetupToken setupToken = new SetupToken(
				UUID.randomUUID().toString(),
				user,
				Instant.now().plusSeconds(60 * 60 * 24)
		);

		return setupTokenRepository.save(setupToken);
	}

	public Optional<SetupToken> validateToken(String token) {

		Optional<SetupToken> tokenOpt = setupTokenRepository.findByTokenAndUsedFalse(token);

		if (tokenOpt.isEmpty()) return Optional.empty();

		SetupToken setupToken = tokenOpt.get();

		if (setupToken.getExpiration().isBefore(Instant.now())) {
			setupTokenRepository.delete(setupToken);
			return Optional.empty();
		}

		return Optional.of(setupToken);
	}

	public void consumeToken(SetupToken token) {
		// instead of deleting the token after use, we mark it as used. This way we can keep a record of token usage for
		// auditing purposes, and we can also prevent any potential issues with token reuse if there are delays in database operations.
		token.setUsed(true);
		setupTokenRepository.save(token);
		logger.info("Setup token consumed for user: {}", token.getUser().getEmail());
	}
	@Transactional
	public void createWorkerWithSetupLink(
			String email,
			String firstName,
			String lastName,
			WorkerClassification classification
	) {

		if (userService.existsByEmail(email)) {
			throw new RuntimeException("Email already exists");
		}

		logger.info("Creating worker with email: {}", email);

		Worker worker = new Worker();
		worker.setEmail(email);
		worker.setFirstName(firstName);
		worker.setLastName(lastName);
		worker.setJobTitle(classification);

		worker.setIsActive(false);
		worker.setPassword(UUID.randomUUID().toString()); // placeholder

		// Save FIRST (important for FK consistency)
		userService.save(worker);

		// THEN assign role
		authorityService.assigningRole(worker, "ROLE_USER");

		// Create token
		SetupToken token = createToken(worker);

		String link = baseUrl + "/setup-password?token=" + token.getToken();

		// Send email
		emailService.sendWorkerSetup(email, link);

		logger.info("Setup email sent to {}", email);
		logger.debug("Setup link: {}", link);
	}

	@Transactional
	public void activateUser(String token, String rawPassword) {

		SetupToken setupToken = validateToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid or expired token"));

		User user = setupToken.getUser();

		if (user.getIsActive()) {
			throw new RuntimeException("User already activated");
		}

		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setIsActive(true);

		userService.save(user);

		consumeToken(setupToken);

		logger.info("User activated: {}", user.getEmail());
	}
}
