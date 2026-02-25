package com.company.cubamant.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
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

	@Column(name = "created_at_customer", nullable = false, updatable = false)
	private Instant createdAtCustomer = Instant.now();

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public void setNewsletterSubscribed(boolean newsletterSubscribed) {
		this.newsletterSubscribed = newsletterSubscribed;
	}

}
