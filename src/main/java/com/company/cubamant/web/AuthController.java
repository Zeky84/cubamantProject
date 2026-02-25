package com.company.cubamant.web;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.cubamant.ab_payload.RefreshTokenRequest;
import com.company.cubamant.ab_payload.TokenRefreshResponse;
import com.company.cubamant.a_security.RefreshTokenService;
import com.company.cubamant.abc_util.CookieUtils;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final RefreshTokenService refreshTokenService;

	public AuthController(RefreshTokenService refreshTokenService) {
		this.refreshTokenService = refreshTokenService;
	}

	@PostMapping("/refresh")
	public ResponseEntity<TokenRefreshResponse> refreshToken(
			@CookieValue(name = "refreshToken", required = false) String refreshToken,
			HttpServletResponse response) {

		if (refreshToken == null) {
			return ResponseEntity.badRequest().build();
		}

		try {
			String newAccessToken = refreshTokenService.createNewAccessToken(
					new RefreshTokenRequest(refreshToken));

			response.addCookie(CookieUtils.createAccessTokenCookie(newAccessToken));

			return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, refreshToken));
		} catch (Exception e) {
			return ResponseEntity.status(401).build();
		}
	}
}
