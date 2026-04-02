package com.company.cubamant.b_service;

import com.company.cubamant.domain.User;
import com.company.cubamant.repository.UserRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RefreshTokenService refreshTokenService;

	public UserServiceImpl(UserRepository userRepository,
						   RefreshTokenService refreshTokenService) {
		this.userRepository = userRepository;
		this.refreshTokenService = refreshTokenService;
	}

	// ✅ SINGLE SOURCE OF AUTHENTICATION
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
	@Secured({"ROLE_ADMIN"})
	public void elevateUserToAdmin(Long id) {
		// TODO: implement role upgrade if needed
	}

	@Override
	@Transactional
	@Secured({"ROLE_ADMIN"})
	public void deleteUser(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found"));

		int deletedTokens = refreshTokenService.deleteByUserId(id);

		userRepository.delete(user);

		System.out.println("deleted Tokens: " + deletedTokens);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
}
