package com.company.cubamant.b_service;

import com.company.cubamant.domain.User;

public interface AuthorityService {
	void assigningRole(User user, String authorityName );
}
