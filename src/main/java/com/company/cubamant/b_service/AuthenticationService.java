package com.company.cubamant.b_service;

import com.company.cubamant.ab_payload.JwtAuthenticationResponse;
import com.company.cubamant.ab_payload.SignInRequest;
import com.company.cubamant.ab_payload.SignUpRequest;
import com.company.cubamant.ab_payload.RegisterRequest;

public interface AuthenticationService {
	JwtAuthenticationResponse signup(RegisterRequest request);
	JwtAuthenticationResponse signin(SignInRequest request);


}
