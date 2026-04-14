package com.company.cubamant.c_web;

import com.company.cubamant.b_service.AuthorityService;
import com.company.cubamant.b_service.SetupService;
import com.company.cubamant.domain.*;
import com.company.cubamant.b_service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
	private final AuthorityService authorityService;

	public AdminController(UserService userService, PasswordEncoder passwordEncoder, SetupService setupService, AuthorityService authorityService) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.setupService = setupService;
		this.authorityService = authorityService;
	}

	@GetMapping("/dashboard")
	public String getDashboard(Model model, Authentication authentication) {
		String adminEmail = authentication.getName();

		List<User> users = userService.findAllWithAuthorities();

		model.addAttribute("users", users);
		model.addAttribute("adminEmail", adminEmail);

		return "admin/dashboard";
	}

	@Transactional
	@PostMapping("/users/{id}/role")
	public String updateUserRole(
			@PathVariable Long id,
			@RequestParam String role,
			Authentication authentication
	) {

		Optional<User> userOpt = userService.findUserById(id);

		if (userOpt.isPresent()) {
			User user = userOpt.get();

			String currentAdminEmail = authentication.getName();

			// 🔒 PREVENT ADMIN FROM DEMOTING THEMSELVES
			if (user.getEmail().equals(currentAdminEmail) && !role.equals("ROLE_ADMIN")) {
				return "redirect:/admin/dashboard?cannotDemoteSelf";
			}

			// Remove existing roles
			user.getAuthoritySet().clear();

			// Assign new role
			authorityService.assigningRole(user, role);
			userService.save(user);
			logger.info("Admin {} updated role of user {} to {}", currentAdminEmail, user.getEmail(), role);
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
			if (user.getEmail().equals(currentUserEmail)) {
				logger.warn("Admin {} attempted to delete themselves", currentUserEmail);
				return "redirect:/admin/dashboard?cannotDeleteSelf";
			}
			userService.deleteUser(id);
		}

		return "redirect:/admin/dashboard";
	}

	@PostMapping("/workers")
	public String createWorker(
			@RequestParam String email,
			@RequestParam String firstName,
			@RequestParam String lastName,
			@RequestParam WorkerClassification classification,
			Model model,
			Authentication authentication
	) {

		try {
			setupService.createWorkerWithSetupLink(
					email, firstName, lastName, classification
			);

			return "redirect:/admin/dashboard?workerCreated";

		} catch (IllegalArgumentException e) {

			// 🔥 reload dashboard with error
			String adminEmail = authentication.getName();
			model.addAttribute("adminEmail", adminEmail);
			model.addAttribute("users", userService.findAllWithAuthorities());

			model.addAttribute("workerError", e.getMessage());

			return "admin/dashboard";
		}
	}
	@PostMapping("/users/{id}/toggle-status")
	public String toggleUserStatus(@PathVariable Long id, Authentication authentication) {

		String currentUserEmail = authentication.getName(); // 👈 inject Authentication

		Optional<User> userOpt = userService.findUserById(id);

		if (userOpt.isPresent()) {
			User user = userOpt.get();

			// 🔒 Prevent admin from disabling themselves
			if (user.getEmail().equals(currentUserEmail)) {
				return "redirect:/admin/dashboard?cannotToggleSelf";
			}

			if (user.getStatus() == AccountStatus.ACTIVE) {
				user.deactivate();
			} else if (user.getStatus() == AccountStatus.DISABLED) {
				user.activate();
			}

			userService.save(user);
		}

		return "redirect:/admin/dashboard";
	}

}
