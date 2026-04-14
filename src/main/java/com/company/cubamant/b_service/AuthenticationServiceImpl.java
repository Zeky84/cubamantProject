package com.company.cubamant.b_service;

import com.company.cubamant.ab_payload.JwtAuthenticationResponse;
import com.company.cubamant.ab_payload.RegisterRequest;
import com.company.cubamant.ab_payload.SignInRequest;
import com.company.cubamant.domain.AccountStatus;
import com.company.cubamant.domain.User;
import com.company.cubamant.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	// This service will handle user authentication logic,such as validating credentials and generating
	// JWT tokens. It will interact with the UserService to retrieve user details and perform authentication checks.
	// Everything to avoid interacting directly in the controller with the userservice
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final RefreshTokenService refreshTokenService;
	private final UserService userService;
	private final AuthorityService authorityService;

	public AuthenticationServiceImpl(UserRepository userRepository,
									 PasswordEncoder passwordEncoder,
									 JwtService jwtService,
									 AuthenticationManager authenticationManager,
									 RefreshTokenService refreshTokenService, UserService userService, AuthorityService authorityService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
		this.refreshTokenService = refreshTokenService;
		this.userService = userService;
		this.authorityService = authorityService;
	}

	@Override
	public JwtAuthenticationResponse signup(RegisterRequest request) {

		if (userService.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("EMAIL_EXISTS");
		}

		User user = new User();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setEmail(request.getEmail());
		user.setStatus(AccountStatus.ACTIVE);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		user.setAuthoritySet(new HashSet<>());
		authorityService.assigningRole(user, "ROLE_USER");

		userService.save(user);

		String jwt = jwtService.generateToken(user);
		var refreshToken = refreshTokenService.createRefreshToken(user.getId());

		return new JwtAuthenticationResponse(jwt, refreshToken.getToken());
	}

	@Override
	public JwtAuthenticationResponse signin(SignInRequest request) {

		var authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.email(),
						request.password()
				)
		);
		User user = (User) authentication.getPrincipal();
		refreshTokenService.deleteByUserId(user.getId());
		var refreshToken = refreshTokenService.createRefreshToken(user.getId());

		String jwt = jwtService.generateToken(user);


		logger.info("User signed in: {}", user.getEmail());

		return new JwtAuthenticationResponse(jwt, refreshToken.getToken());
	}


}
