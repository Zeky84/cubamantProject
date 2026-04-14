package com.company.cubamant.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.company.cubamant.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
	@Query("""
    SELECT u FROM User u
    JOIN FETCH u.authoritySet
    WHERE u.email = :email
""")
	Optional<User> findByEmailWithAuthorities(String email);

	@Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.authoritySet")
	List<User> findAllWithAuthorities();
}
