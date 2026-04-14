package com.company.cubamant.da_validator;

import com.company.cubamant.ab_payload.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegisterRequest> {
	@Override
	public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
		if (request.getPassword() == null || request.getConfirmPassword() == null) {
			return true; // let @NotBlank handle the null case
		}
		return request.getPassword().equals(request.getConfirmPassword());
	}
}
