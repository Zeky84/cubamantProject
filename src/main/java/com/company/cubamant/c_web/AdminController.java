package com.company.cubamant.c_web;

import com.company.cubamant.domain.Authority;
import com.company.cubamant.domain.User;
import com.company.cubamant.b_service.UserService;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	public AdminController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@PostConstruct
	public void init() {
		createDefaultAdmin();
	}

	private void createDefaultAdmin() {
		if (userService.findUserByEmail("admin@cubamant.com").isEmpty()) {
			User admin = new User();
			admin.setEmail("admin@cubamant.com");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setFirstName("Admin");
			admin.setLastName("User");
			admin.setIsActive(true);
			admin.setCreatedAt(Instant.now());

			admin.getAuthoritySet().add(new Authority("ROLE_ADMIN", admin));

			userService.save(admin);
			logger.info("Default admin user created: admin@cubamant.com");
		}
	}

	@GetMapping("/dashboard")
	public String getDashboard(Model model, Authentication authentication) {
		String adminEmail = authentication.getName();
		List<User> users = userService.findAll();

		model.addAttribute("users", users);
		model.addAttribute("adminEmail", adminEmail);

		return "admin/dashboard"; // templates/admin/dashboard.html
	}

	@PostMapping("/users/{userId}/elevate")
	public String elevateToAdmin(@PathVariable Long userId) {
		userService.elevateUserToAdmin(userId);
		return "redirect:/admin/dashboard";
	}

	@PostMapping("/users/{userId}/delete")
	public String deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);
		return "redirect:/admin/dashboard";
	}
}
