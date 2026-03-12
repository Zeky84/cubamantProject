package com.company.cubamant.b_service;
import com.company.cubamant.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.company.cubamant.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) {
				return userRepository.findByEmail(username)
						.orElseThrow(() -> new UsernameNotFoundException("User not found"));
			}
		};
	}

	@Override
	public Optional<User> findUserByEmail(String email) {
		return Optional.empty();
	}

	@Override
	public void save(User admin) {

	}

	@Override
	public List<User> findAll() {
		return null;
	}

	@Override
	public void elevateUserToAdmin(Long userId) {

	}

	@Override
	public void deleteUser(Long userId) {

	}


}
