package com.company.cubamant.b_service;

import com.company.cubamant.domain.Authority;
import com.company.cubamant.domain.User;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {
	@Override
	public void assigningRole(User user,String authorityName ) {
		Authority authority = new Authority(authorityName, user);
		user.addAuthority(authority);
//		user.getAuthoritySet().add(new Authority(authorityName, user));
	}
}
