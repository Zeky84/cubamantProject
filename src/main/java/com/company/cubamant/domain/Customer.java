package com.company.cubamant.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "customers")
public class Customer extends User {

	@Column(name = "shipping_address")
	private String shippingAddress;

	@Column(name = "newsletter_subscribed", nullable = false)
	private boolean newsletterSubscribed = true;

	@Column(name = "created_at_customer", nullable = false, updatable = false)
	private Instant createdAtCustomer = Instant.now();

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

	public Instant getCreatedAtCustomer() {
		return createdAtCustomer;
	}

	public void setCreatedAtCustomer(Instant createdAtCustomer) {
		this.createdAtCustomer = createdAtCustomer;
	}

	// Add other customer-specific fields here
}
