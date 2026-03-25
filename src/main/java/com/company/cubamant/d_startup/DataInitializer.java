package com.company.cubamant.d_startup;

import com.company.cubamant.c_web.AdminController;
import com.company.cubamant.domain.Authority;
import com.company.cubamant.domain.User;
import com.company.cubamant.b_service.UserService;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

import java.time.Instant;

@Component
public class DataInitializer {
	// This class is responsible for initializing the database with default data during application startup, such as creating a default admin user if it doesn't already exist. This ensures that there is always an admin user available to manage the application after deployment.
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	public DataInitializer(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@PostConstruct
	public void init() {
		createDefaultAdmin();
	}

	private void createDefaultAdmin() {
			if (userService.findUserByEmail("e").isEmpty()) {
				User admin = new User();
				admin.setEmail("e");
				admin.setPassword(passwordEncoder.encode("e"));
				admin.setFirstName("Admin");
				admin.setLastName("User");
				admin.setIsActive(true);
				admin.setCreatedAt(Instant.now());

				admin.getAuthoritySet().add(new Authority("ROLE_ADMIN", admin));

				userService.save(admin);
				logger.info("Default admin user created: admin@cubamant.com / admin123");
			}
	}
}
