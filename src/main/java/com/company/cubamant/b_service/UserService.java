package com.company.cubamant.b_service;

import com.company.cubamant.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

	Optional<User> findUserByEmail(String email);

	void save(User user);

	List<User> findAll();

	void deleteUser(Long userId);

	Optional<User> findUserById(Long userId);

	boolean existsByEmail(String email);


	List<User> findAllWithAuthorities();
}
