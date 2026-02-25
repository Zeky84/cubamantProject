package com.company.cubamant.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends User {

	@Column(name = "company_name")
	private String companyName;

	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	private List<Project> projects;

	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	private List<Review> reviews;

	@Column(name = "shipping_address")
	private String shippingAddress;

	@Column(name = "newsletter_subscribed", nullable = false)
	private boolean newsletterSubscribed = true;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public boolean isNewsletterSubscribed() {
		return newsletterSubscribed;
	}

	public void setNewsletterSubscribed(boolean newsletterSubscribed) {
		this.newsletterSubscribed = newsletterSubscribed;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
