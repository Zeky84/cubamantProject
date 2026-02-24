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

	// Add other customer-specific fields here
}
