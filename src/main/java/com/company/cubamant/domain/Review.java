package com.company.cubamant.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Min(1)
	@Max(5)
	private int rating; // 1–5

	@Column(length = 1000)
	private String comment;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Enumerated(EnumType.STRING)
	private ReviewVisibility visibility;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Customer customer;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Project project;

	@ElementCollection
	@CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
	@Column(name = "image_url")
	private List<String> imageUrls;

	public void setId(Long id) {
		this.id = id;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setVisibility(ReviewVisibility visibility) {
		this.visibility = visibility;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}
}
