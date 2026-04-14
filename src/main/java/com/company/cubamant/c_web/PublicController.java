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
	public String signup(@Valid @ModelAttribute("request") RegisterRequest request,
						 BindingResult bindingResult,
						 Model model) {

		if (bindingResult.hasErrors()) {
			// Spring MVC already populated field errors from @Valid
			return "signup"; // return to form view with errors
		}

		try {
			authenticationService.signup(request);
			return "redirect:/signin?registered";

		} catch (IllegalArgumentException e) {
			// Only business logic exceptions should reach here now
			if ("EMAIL_EXISTS".equals(e.getMessage())) {
				model.addAttribute("emailError", "Email already registered");
			}
			return "signup";
		}
	}
}
