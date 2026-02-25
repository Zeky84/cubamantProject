package com.company.cubamant.a_security;

import com.company.cubamant.ab_payload.JwtAuthenticationResponse;
import com.company.cubamant.ab_payload.SignInRequest;
import com.company.cubamant.ab_payload.SignUpRequest;

public interface AuthenticationService {
	JwtAuthenticationResponse signup(SignUpRequest request);
	JwtAuthenticationResponse signin(SignInRequest request);
}
