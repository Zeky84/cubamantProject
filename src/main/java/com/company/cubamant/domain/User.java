package com.company.cubamant.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	private String firstName;
	private String lastName;

	// =========================
	// LIFECYCLE STATE (IMPORTANT)
	// =========================
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountStatus status;

	// =========================
	// AUDIT
	// =========================
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "activated_at")
	private Instant activatedAt;

	// Optional legacy/debug field (you be remove later safely)
	@Column(name = "user_type", insertable = false, updatable = false)
	private String userType;

	// =========================
	// ROLES / AUTHORITIES
	// =========================
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<Authority> authoritySet = new HashSet<>();

	@PrePersist
	public void prePersist() {
		this.createdAt = Instant.now();

		if (this.status == null) {
			this.status = AccountStatus.PENDING_SETUP;
		}
	}

	// ===== Helpers =====

	public void activate() {
		this.status = AccountStatus.ACTIVE;
		this.activatedAt = Instant.now();
	}

	public void deactivate() {
		this.status = AccountStatus.DISABLED;
	}

	public boolean isActiveUser() {
		return this.status == AccountStatus.ACTIVE;
	}

	// ===== Authority helpers =====

	public void addAuthority(Authority authority) {
		authoritySet.add(authority);
		authority.setUser(this);
	}

	public void removeAuthority(Authority authority) {
		authoritySet.remove(authority);
		authority.setUser(null);
	}

	// ===== Spring Security =====

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authoritySet.stream()
				.map(a -> new org.springframework.security.core.authority.SimpleGrantedAuthority(a.getAuthority()))
				.toList();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return status != AccountStatus.DISABLED;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return status == AccountStatus.ACTIVE;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getActivatedAt() {
		return activatedAt;
	}

	public void setActivatedAt(Instant activatedAt) {
		this.activatedAt = activatedAt;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Set<Authority> getAuthoritySet() {
		return authoritySet;
	}

	public void setAuthoritySet(Set<Authority> authoritySet) {
		this.authoritySet = authoritySet;
	}
}
