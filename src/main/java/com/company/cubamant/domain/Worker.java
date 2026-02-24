package com.company.cubamant.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "workers")
public class Worker extends User {

	@Column(name = "role")
	private String role; // e.g., “engineer”, “admin”

	@Column(name = "hourly_rate")
	private BigDecimal hourlyRate;

	@Column(name = "is_supervisor", nullable = false)
	private boolean isSupervisor = false;

	@Column(name = "created_at_worker", nullable = false, updatable = false)
	private Instant createdAtWorker = Instant.now();

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public BigDecimal getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(BigDecimal hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public boolean isSupervisor() {
		return isSupervisor;
	}

	public void setSupervisor(boolean supervisor) {
		isSupervisor = supervisor;
	}

	public Instant getCreatedAtWorker() {
		return createdAtWorker;
	}

	public void setCreatedAtWorker(Instant createdAtWorker) {
		this.createdAtWorker = createdAtWorker;
	}
}
