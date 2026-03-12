package com.company.cubamant.c_web;
import com.company.cubamant.b_service.AuthenticationServiceImpl;
import com.company.cubamant.b_service.AuthenticationService;
import com.company.cubamant.ab_payload.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PublicController {

	private final AuthenticationService authenticationService;
	private PasswordEncoder passwordEncoder;


	public PublicController( AuthenticationServiceImpl authenticationService, PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
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
	public String signup(ModelMap model) {
		model.addAttribute("request",new RegisterRequest());
		return "signup";
	}
	@PostMapping("/signup")
	public String signupPost(@ModelAttribute("request") RegisterRequest request, Model model) {
		// UI validation
		if (!request.getPassword().equals(request.getConfirmPassword())) {
			model.addAttribute("passwordMismatch", true);
			return "signup";
		}

		try {
			authenticationService.signup(request);
			return "redirect:/signin";

		} catch (RuntimeException e) {
			model.addAttribute("userExists", true);
			return "signup";
		}
	}
}
