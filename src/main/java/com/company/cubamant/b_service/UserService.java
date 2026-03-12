package com.company.cubamant.b_service;

import com.company.cubamant.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService {
	UserDetailsService userDetailsService();

	Optional<User> findUserByEmail(String email);

	void save(User admin);

	List<User> findAll();

	void elevateUserToAdmin(Long id);

	void deleteUser(Long id);

	Optional<User> findUserById(Long id);

	boolean existsByEmail(String email);
}
