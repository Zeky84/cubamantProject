package com.company.cubamant.domain;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "authority")
public class Authority implements GrantedAuthority {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "role", nullable = false)
	private String authority;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Authority() {}

	public Authority(String authority, User user) {
		this.authority = authority;
		this.user = user;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	@Override
	public String getAuthority() { return authority; }
	public void setAuthority(String authority) { this.authority = authority; }

	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }
}
