package com.company.cubamant.domain;
import jakarta.persistence.*;

@Entity
@Table(name = "authorities")
public class Authority {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String authority; // ROLE_USER, ROLE_ADMIN, etc.

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// constructors
	public Authority() {}

	public Authority(String authority, User user) {
		this.authority = authority;
		this.user = user;
	}

	// getters & setters
}
