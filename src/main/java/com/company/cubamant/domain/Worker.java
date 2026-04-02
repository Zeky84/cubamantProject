package com.company.cubamant.domain;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity

@Table(name = "workers")
public class Worker extends User {

	@Enumerated(EnumType.STRING)
	@Column(name = "job_title")
	private WorkerClassification jobTitle;

	@Column(name = "hourly_rate")
	private BigDecimal hourlyRate;

	@Column(name = "is_supervisor")
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
