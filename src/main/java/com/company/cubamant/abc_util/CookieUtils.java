package com.company.cubamant.abc_util;
import jakarta.servlet.http.Cookie;

public class CookieUtils {

	private static final int ACCESS_TOKEN_MAX_AGE = 15 * 60; // 15 minutes
	private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7 days

	public static Cookie createAccessTokenCookie(String value) {
		Cookie cookie = new Cookie("accessToken", value);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(ACCESS_TOKEN_MAX_AGE);
		cookie.setAttribute("SameSite", "Strict");
		return cookie;
	}

	public static Cookie createRefreshTokenCookie(String value) {
		Cookie cookie = new Cookie("refreshToken", value);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
		cookie.setAttribute("SameSite", "Strict");
		return cookie;
	}

	public static Cookie deleteCookie(String name) {
		Cookie cookie = new Cookie(name, null);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		return cookie;
	}
}
