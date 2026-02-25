package com.company.cubamant.domain;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Entity
public class Authority implements GrantedAuthority {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role authority;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Authority () {}

	public Authority(Role authority) {
		super();
		this.authority = authority;
	}

	public Authority(Role auth, User user) {
		this.authority = auth;
		this.user = user;
	}

	@Override
	public String toString() {
		return "Authority [id=" + id + ", authority=" + authority + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Authority authority = (Authority) o;
		return Objects.equals(id, authority.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String getAuthority() {
		return authority.name();
	}

	public void setAuthority(Role authority) {
		this.authority = authority;
	}
}
