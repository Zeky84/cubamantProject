package com.company.cubamant.c_web;

import com.company.cubamant.ab_payload.RegisterRequest;
import com.company.cubamant.b_service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class PublicController {

	private final AuthenticationService authenticationService;

	public PublicController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@GetMapping("/signin")
	public String signin() {
		return "signin";
	}

	@GetMapping("/")
	public String home() {
		return "redirect:/signin";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("request", new RegisterRequest());
		return "signup";
	}

	@PostMapping("/signup")
	public String signupPost(
			@Valid @ModelAttribute("request") RegisterRequest request,
			BindingResult bindingResult,
			Model model) {

		//  FIELD VALIDATION (automatic)
		if (bindingResult.hasErrors()) {
			return "signup";
		}

		//  CROSS-FIELD VALIDATION
		if (!request.getPassword().equals(request.getConfirmPassword())) {
			model.addAttribute("passwordMismatch", true);
			return "signup";
		}

		try {
			authenticationService.signup(request);
			return "redirect:/signin";

		} catch (IllegalArgumentException e) {

			if ("EMAIL_EXISTS".equals(e.getMessage())) {
				model.addAttribute("userExists", true);
			} else {
				model.addAttribute("genericError", true);
			}

			return "signup";
		}
	}
}
