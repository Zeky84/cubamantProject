package com.company.cubamant.b_service;

import com.company.cubamant.c_web.AdminController;
import com.company.cubamant.domain.User;
import com.company.cubamant.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RefreshTokenService refreshTokenService;
	private final SetupTokenService setupTokenService;
	private final AuthorityService authorityService;
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	public UserServiceImpl(UserRepository userRepository,
						   RefreshTokenService refreshTokenService, SetupTokenService setupTokenService, AuthorityService authorityService) {
		this.userRepository = userRepository;
		this.refreshTokenService = refreshTokenService;
		this.setupTokenService = setupTokenService;
		this.authorityService = authorityService;
	}

	// SINGLE SOURCE OF AUTHENTICATION
	@Override
	public UserDetails loadUserByUsername(String email) {
		return userRepository.findByEmailWithAuthorities(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Override
	public Optional<User> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void save(User user) {
		userRepository.save(user);
	}

	@Override
	@Secured({"ROLE_ADMIN"})
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> findUserById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	@Transactional
	@Secured({"ROLE_ADMIN"})
	public void deleteUser(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found"));

		setupTokenService.deleteByUser(user);
		int deletedTokens = refreshTokenService.deleteByUserId(id);
		userRepository.delete(user);

		logger.info("Deleted {} refresh tokens for userId: {}", deletedTokens, id);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public List<User> findAllWithAuthorities() {
		return userRepository.findAllWithAuthorities();
	}

	@Override
	@Transactional
	@Secured("ROLE_ADMIN")
	public void updateUserRole(Long userId, String newRole, String currentAdminEmail) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (user.getEmail().equals(currentAdminEmail) && !newRole.equals("ROLE_ADMIN")) {
			throw new IllegalArgumentException("CANNOT_DEMOTE_SELF");
		}

		// Short-circuit: user already has this role, nothing to do
		boolean alreadyHasRole = user.getAuthoritySet().stream()
				.anyMatch(a -> a.getAuthority().equals(newRole));

		if (alreadyHasRole) {
			logger.info("User {} already has role {}, skipping update", user.getEmail(), newRole);
			return;
		}

		user.getAuthoritySet().clear();
		userRepository.saveAndFlush(user); // Force DELETE to DB before INSERT

		authorityService.assigningRole(user, newRole);
		userRepository.save(user);

		logger.info("Admin {} updated role of user {} to {}", currentAdminEmail, user.getEmail(), newRole);
	}
}
