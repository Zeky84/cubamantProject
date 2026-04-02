package com.company.cubamant.c_web;

import com.company.cubamant.b_service.SetupService;
import com.company.cubamant.domain.SetupToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class SetupController {

	private final SetupService setupService;

	public SetupController(SetupService setupService) {
		this.setupService = setupService;
	}

	@GetMapping("/setup-password")
	public String showSetupForm(@RequestParam("token") String token, Model model) {

		Optional<SetupToken> tokenOpt = setupService.validateToken(token);

		if (tokenOpt.isEmpty()) {
			model.addAttribute("error", "Invalid or expired token");
			return "setup-password";
		}

		model.addAttribute("token", token);
		return "setup-password";
	}

	@PostMapping("/setup-password")
	public String processSetup(
			@RequestParam String token,
			@RequestParam String password,
			Model model
	) {
		try {
			setupService.activateUser(token, password);
			return "redirect:/signin?setupSuccess";

		} catch (Exception e) {
			model.addAttribute("error", "Invalid or expired token");
			model.addAttribute("token", token);
			return "setup-password";
		}
	}
}
