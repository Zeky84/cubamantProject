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

	private Boolean isActive;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	// Optional: keep for debugging/reporting
	@Column(name = "user_type", insertable = false, updatable = false)
	private String userType;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@Builder.Default
	private Set<Authority> authoritySet = new HashSet<>();

	@PrePersist
	public void prePersist() {
		this.createdAt = Instant.now();
	}

	// ===== Bidirectional helpers =====
	public void addAuthority(Authority authority) {
		authoritySet.add(authority);
		authority.setUser(this);
	}

	public void removeAuthority(Authority authority) {
		authoritySet.remove(authority);
		authority.setUser(null);
	}

	// ===== Security =====
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authoritySet.stream()
				.map(a -> new org.springframework.security.core.authority.SimpleGrantedAuthority(a.getAuthority()))
				.toList();
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override public boolean isAccountNonExpired() { return true; }
	@Override public boolean isAccountNonLocked() { return true; }
	@Override public boolean isCredentialsNonExpired() { return true; }

	@Override
	public boolean isEnabled() {
		return Boolean.TRUE.equals(isActive);
	}

	// ===== Getters / Setters =====
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	@Override
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }

	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }

	public Boolean getIsActive() { return isActive; }
	public void setIsActive(Boolean active) { isActive = active; }

	public Instant getCreatedAt() { return createdAt; }
	public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

	public Set<Authority> getAuthoritySet() { return authoritySet; }
	public void setAuthoritySet(Set<Authority> authoritySet) { this.authoritySet = authoritySet; }

	public String getUserType() { return userType; }
}
