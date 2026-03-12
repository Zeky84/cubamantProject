package com.company.cubamant.domain;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity

@Table(name = "workers")
public class Worker extends User {

	@Column(name = "job_title")
	private WorkerClassification jobTitle; // e.g., “engineer”, “admin”

	@Getter
	@Column(name = "hourly_rate",nullable = false)
	private BigDecimal hourlyRate;

	@Column(name = "is_supervisor", nullable = false)
	private boolean isSupervisor = false;


	public WorkerClassification getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(WorkerClassification jobTitle) {
		this.jobTitle = jobTitle;
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


}
