package com.company.cubamant.domain;
import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 500)
	private String bio;

	@Column(name = "avatar_url")
	private String avatarUrl;

	@Column(name = "is_public", nullable = false)
	private boolean isPublic = true;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// getters & setters
}
