package com.company.cubamant.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "setup_tokens")
public class SetupToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String token;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private Instant expiration;

	public SetupToken() {}

	public SetupToken(String token, User user, Instant expiration) {
		this.token = token;
		this.user = user;
		this.expiration = expiration;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Instant getExpiration() {
		return expiration;
	}

	public void setExpiration(Instant expiration) {
		this.expiration = expiration;
	}

	// getters/setters
}
