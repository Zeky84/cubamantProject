package com.company.cubamant.b_service;

import com.company.cubamant.ab_payload.JwtAuthenticationResponse;
import com.company.cubamant.ab_payload.RegisterRequest;
import com.company.cubamant.ab_payload.SignInRequest;
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

// COMMENTED CAUSE OF THE CHANGE TO THE SIGNUP METHOD TO USE THE RegisterRequest DTO instead of the SignUpRequest RECORD
// DTO OVER DTO-RECORD TO AVOID THYMELEAF PROBLEMS
//	@Override
//	public JwtAuthenticationResponse signup(SignUpRequest request) {
//
//		if (userService.existsByEmail(request.email())) {
//			throw new RuntimeException("Email already exists");
//		}
//		// Create user without builder
//		User user = new User();
//		user.setFirstName(request.firstName());
//		user.setLastName(request.lastName());
//		user.setEmail(request.email());
//		user.setPassword(passwordEncoder.encode(request.password()));
//
//		// Initialize the set
//		user.setAuthoritySet(new HashSet<>());
//
//		// Add authorities
//		user.getAuthoritySet().add(new Authority("USER", user));
//
//		request.authorityOpt().ifPresent(auth ->
//				user.getAuthoritySet().add(new Authority(auth, user)));
//
//		userRepository.save(user);
//
//		String jwt = jwtService.generateToken(user);
//		var refreshToken = refreshTokenService.createRefreshToken(user.getId());
//
//		logger.info("New user registered: {}", user.getEmail());
//
//		return new JwtAuthenticationResponse(jwt, refreshToken.getToken());
//	}

	@Override
	public JwtAuthenticationResponse signup(RegisterRequest request) {
		//Using RegisterRequest DTO to avoid using the User entity directly for security and separation of concerns.
	if (userService.existsByEmail(request.getEmail())) {
		throw new RuntimeException("Email already exists");
	}
	if (!request.getPassword().equals(request.getConfirmPassword())) {
			throw new RuntimeException("Passwords do not match");
		}

	User user = new User();
	user.setFirstName(request.getFirstName());
	user.setLastName(request.getLastName());
	user.setEmail(request.getEmail());
	user.setIsActive(true);
	user.setPassword(passwordEncoder.encode(request.getPassword()));

	user.setAuthoritySet(new HashSet<>());
	authorityService.assigningRole(user,"ROLE_USER");

	userService.save(user);

	String jwt = jwtService.generateToken(user);
	var refreshToken = refreshTokenService.createRefreshToken(user.getId());

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
