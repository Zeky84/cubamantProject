package com.company.cubamant.c_web;

import com.company.cubamant.b_service.SetupService;
import com.company.cubamant.b_service.UserService;
import com.company.cubamant.domain.SetupToken;
import com.company.cubamant.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SetupController {

	private final SetupService setupService;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	public SetupController(SetupService setupService,
						   UserService userService,
						   PasswordEncoder passwordEncoder) {
		this.setupService = setupService;
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/setup-password")
	public String showSetupForm(@RequestParam String token, Model model) {
		model.addAttribute("token", token);
		return "auth/setup-password";
	}

	@PostMapping("/setup-password")
	public String handleSetup(
			@RequestParam String token,
			@RequestParam String password
	) {

		SetupToken setupToken = setupService.validateToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid token"));

		User user = setupToken.getUser();

		user.setPassword(passwordEncoder.encode(password));
		user.setIsActive(true); // 🔥 activation happens HERE

		userService.save(user);

		return "redirect:/signin?setupSuccess";
	}
}
