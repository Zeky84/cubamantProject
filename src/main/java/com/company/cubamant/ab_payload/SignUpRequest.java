package com.company.cubamant.ab_payload;
import java.util.Optional;

public record SignUpRequest(
		String email,
		String password,
		String firstName,
		String lastName,
		Optional<String> authorityOpt
) {}
