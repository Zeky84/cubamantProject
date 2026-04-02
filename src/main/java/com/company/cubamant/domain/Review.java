package com.company.cubamant.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "review")
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Min(1)
	@Max(5)
	private int rating;

	@Column(length = 1000)
	private String comment;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Enumerated(EnumType.STRING)
	private ReviewVisibility visibility;

	@ManyToOne(fetch = FetchType.LAZY) // ✅ FIX
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY) // ✅ FIX
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@ElementCollection(fetch = FetchType.LAZY) // ✅ FIX
	@CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
	@Column(name = "image_url")
	private List<String> imageUrls;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public ReviewVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(ReviewVisibility visibility) {
		this.visibility = visibility;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}
}
