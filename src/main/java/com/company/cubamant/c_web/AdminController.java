package com.company.cubamant.c_web;

import com.company.cubamant.b_service.SetupService;
import com.company.cubamant.domain.*;
import com.company.cubamant.b_service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	private final SetupService setupService;
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	public AdminController(UserService userService, PasswordEncoder passwordEncoder, SetupService setupService) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.setupService = setupService;
	}

	@GetMapping("/dashboard")
	public String getDashboard(Model model, Authentication authentication) {
		String adminEmail = authentication.getName();
		List<User> users = userService.findAll();

		model.addAttribute("users", users);
		model.addAttribute("adminEmail", adminEmail);

		return "admin/dashboard";
	}

	@PostMapping("/users/{id}/elevate")
	public String elevateToAdmin(@PathVariable Long id) {
		Optional<User> userOpt = userService.findUserById(id);

		if (userOpt.isPresent()) {
			User user = userOpt.get();

			// Check if user already has admin role
			boolean hasAdminRole = user.getAuthoritySet().stream()
					.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

			if (!hasAdminRole) {
				user.getAuthoritySet().add(new Authority("ROLE_ADMIN", user));
				userService.save(user);
				logger.info("User {} elevated to admin", user.getEmail());
			}
		}

		return "redirect:/admin/dashboard";
	}

	@PostMapping("/users/{id}/delete")
	public String deleteUser(@PathVariable Long id, Authentication authentication) {
		String currentUserEmail = authentication.getName();

		Optional<User> userOpt = userService.findUserById(id);

		if (userOpt.isPresent()) {
			User user = userOpt.get();

			// Prevent deleting yourself
			if (!user.getEmail().equals(currentUserEmail)) {
				userService.deleteUser(id);
				logger.info("User {} deleted by admin {}", user.getEmail(), currentUserEmail);
			} else {
				logger.warn("Admin {} attempted to delete themselves", currentUserEmail);
			}
		}

		return "redirect:/admin/dashboard";
	}

	@PostMapping("/workers")
	public String createWorker(
			@RequestParam String email,
			@RequestParam String firstName,
			@RequestParam String lastName,
			@RequestParam WorkerClassification classification
	) {

		if (userService.existsByEmail(email)) {
			return "redirect:/admin/dashboard?error=emailExists";
		}

		Worker worker = new Worker();
		worker.setEmail(email);
		worker.setFirstName(firstName);
		worker.setLastName(lastName);
		worker.setJobTitle(classification);
		worker.setIsActive(false); // 🔴 critical
		worker.setPassword("");   // no password yet

		worker.getAuthoritySet().add(new Authority("ROLE_USER", worker));

		userService.save(worker);

		SetupToken token = setupService.createToken(worker);

		String link = "http://localhost:8080/setup-password?token=" + token.getToken();

		logger.info("Onboarding link: {}", link);

		return "redirect:/admin/dashboard?workerCreated";
	}
}
