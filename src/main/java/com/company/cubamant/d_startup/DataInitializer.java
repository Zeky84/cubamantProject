package com.company.cubamant.d_startup;

import com.company.cubamant.b_service.AuthorityService;
import com.company.cubamant.c_web.AdminController;
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
	private final AuthorityService authorityService;
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	public DataInitializer(UserService userService, PasswordEncoder passwordEncoder, AuthorityService authorityService) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.authorityService = authorityService;
	}

	@PostConstruct
	public void init() {
		createDefaultAdmin();
	}

	private void createDefaultAdmin() {
			if (userService.findUserByEmail("admin").isEmpty()) {
				User admin = new User();
				admin.setEmail("admin");
				admin.setPassword(passwordEncoder.encode("admin"));
				admin.setFirstName("admin");
				admin.setLastName("admin");
				admin.setIsActive(true);
				admin.setCreatedAt(Instant.now());
				authorityService.assigningRole(admin,"ROLE_ADMIN");


				userService.save(admin);
				logger.info("Default admin user created: admin / admin");
			}
	}
}
