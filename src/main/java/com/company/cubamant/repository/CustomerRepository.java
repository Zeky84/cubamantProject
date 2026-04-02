package com.company.cubamant.repository;

import com.company.cubamant.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	@Query("""
			    SELECT c FROM Customer c
			    WHERE c.id = :id
			""")
	Optional<Customer> findSimpleCustomer(@Param("id") Long id);

	@Query("""
			    SELECT c FROM Customer c
			    LEFT JOIN FETCH c.projects
			    WHERE c.id = :id
			""")
	Optional<Customer> findWithProjects(Long id);
}
