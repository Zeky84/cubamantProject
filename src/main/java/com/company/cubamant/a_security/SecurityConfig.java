package com.company.cubamant.a_security;
import com.company.cubamant.b_service.JwtService;
import com.company.cubamant.domain.RefreshToken;
import com.company.cubamant.domain.User;
import com.company.cubamant.b_service.RefreshTokenService;
import com.company.cubamant.b_service.UserService;
import com.company.cubamant.abc_util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final UserService userService;
	private final UserDetailsService userDetailsService;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
						  UserService userService,
						  UserDetailsService userDetailsService, JwtService jwtService,
						  RefreshTokenService refreshTokenService) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.userService = userService;
		this.userDetailsService = userDetailsService;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
		requestHandler.setCsrfRequestAttributeName("_csrf");

		http
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						.csrfTokenRequestHandler(requestHandler)
						.ignoringRequestMatchers(
								"/api/**",
								"/signin",
								"/registration"
						)
				)
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((request, response, authException) ->
								response.sendRedirect("/error?unauthenticated=true"))
						.accessDeniedHandler((request, response, accessDeniedException) ->
								response.sendRedirect("/error?unauthorized=true"))
				)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN") // Acepta ambos formatos
						.requestMatchers("/api/user/**").hasAnyAuthority("ROLE_USER", "USER","ROLE_SUPERUSER", "SUPERUSER", "ROLE_ADMIN", "ADMIN")
						.requestMatchers("/", "/signin", "/signup", "/error", "/css/**", "/js/**,","/setup-password/**").permitAll()
						.anyRequest().authenticated()
				)
				.sessionManagement(session ->
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.formLogin(login -> login
						.loginPage("/signin")
						.usernameParameter("email")
						.successHandler(this::handleAuthSuccess)
						.failureHandler(this::handleAuthFailure)
						.permitAll()
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/signin")//todo: change to /homepage once the the home view is set-up
						.deleteCookies("accessToken", "refreshToken", "JSESSIONID", "XSRF-TOKEN")
						.invalidateHttpSession(true)
						.clearAuthentication(true)
				);

		return http.build();
	}

	private void handleAuthSuccess(HttpServletRequest request,
								   HttpServletResponse response,
								   Authentication authentication) throws IOException {
		User user = (User) authentication.getPrincipal();
		user.getAuthorities().forEach(a ->
				logger.info("ROLE FOUND: {}", a.getAuthority())
		);
		String accessToken = jwtService.generateToken( user);
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

		Cookie accessCookie = CookieUtils.createAccessTokenCookie(accessToken);
		Cookie refreshCookie = CookieUtils.createRefreshTokenCookie(refreshToken.getToken());

		response.addCookie(accessCookie);
		response.addCookie(refreshCookie);

		logger.info("Successful authentication for user: {}", user.getUsername());

		boolean isAdmin = user.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().replace("ROLE_", "").equals("ADMIN"));

		if (isAdmin) {
			logger.info("Admin detectado. Redirigiendo a /admin/dashboard");
			response.sendRedirect("/admin/dashboard");
		} else {
			logger.info("Usuario detectado. Redirigiendo a /user/dashboard");
			response.sendRedirect("/user/dashboard");
		}
	}

	private void handleAuthFailure(HttpServletRequest request,
								   HttpServletResponse response,
								   org.springframework.security.core.AuthenticationException exception)
			throws IOException {
		String email = request.getParameter("email");
		logger.warn("Authentication failed for email: {}", email);
		response.sendRedirect("/signin?authError");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
			throws Exception {
		return config.getAuthenticationManager();
	}
}
