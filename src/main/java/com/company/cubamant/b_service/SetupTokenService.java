package com.company.cubamant.b_service;

import com.company.cubamant.domain.User;
import org.springframework.stereotype.Service;

import com.company.cubamant.repository.SetupTokenRepository;
@Service
public class SetupTokenService {

	private final SetupTokenRepository setupTokenRepository;

	public SetupTokenService(SetupTokenRepository setupTokenRepository) {
		this.setupTokenRepository = setupTokenRepository;
	}

	public void deleteByUserId(Long userId) {
		setupTokenRepository.deleteByUserId(userId);
	}
	public void deleteByUser(User user){
		setupTokenRepository.deleteByUser(user);
	}
}
