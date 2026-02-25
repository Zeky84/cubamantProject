package com.company.cubamant.a_security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.company.cubamant.domain.Authority;
import com.company.cubamant.domain.User;
import com.company.cubamant.ab_payload.SignInRequest;
import com.company.cubamant.ab_payload.SignUpRequest;
import com.company.cubamant.ab_payload.JwtAuthenticationResponse;
import com.company.cubamant.repository.UserRepository;
import com.company.cubamant.a_security.RefreshTokenService;

import java.util.HashSet;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final RefreshTokenService refreshTokenService;

	public AuthenticationServiceImpl(UserRepository userRepository,
									 PasswordEncoder passwordEncoder,
									 JwtService jwtService,
									 AuthenticationManager authenticationManager,
									 RefreshTokenService refreshTokenService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
		this.refreshTokenService = refreshTokenService;
	}

	@Override
	public JwtAuthenticationResponse signup(SignUpRequest request) {
		// Create user without builder
		User user = new User();
		user.setFirstName(request.firstName());
		user.setLastName(request.lastName());
		user.setEmail(request.email());
		user.setPassword(passwordEncoder.encode(request.password()));

		// Initialize the set
		user.setAuthoritySet(new HashSet<>());

		// Add authorities
		user.getAuthoritySet().add(new Authority("ROLE_USER", user));

		request.authorityOpt().ifPresent(auth ->
				user.getAuthoritySet().add(new Authority(auth, user)));

		userRepository.save(user);

		String jwt = jwtService.generateToken(user);
		var refreshToken = refreshTokenService.createRefreshToken(user.getId());

		logger.info("New user registered: {}", user.getEmail());

		return new JwtAuthenticationResponse(jwt, refreshToken.getToken());
	}

	@Override
	public JwtAuthenticationResponse signin(SignInRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

		String jwt = jwtService.generateToken(user);

		var refreshToken = refreshTokenService.findByUserId(user.getId())
				.orElseGet(() -> refreshTokenService.createRefreshToken(user.getId()));

		logger.info("User signed in: {}", user.getEmail());

		return new JwtAuthenticationResponse(jwt, refreshToken.getToken());
	}
}
