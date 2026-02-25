package com.company.cubamant.domain;
import jakarta.persistence.*;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Entity
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate startDate;
	private LocalDate endDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProjectStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus paymentStatus;

	@Column(nullable = false)
	private BigDecimal totalCost;

	private BigDecimal materialCost;
	private BigDecimal laborCost;

	private Integer scheduledHours; // scheduled total hours by project

	@ManyToOne
	@JoinColumn(nullable = false)
	private Customer customer;

	@ManyToMany
	@JoinTable(
			name = "project_workers",
			joinColumns = @JoinColumn(name = "project_id"),
			inverseJoinColumns = @JoinColumn(name = "worker_id")
	)

	private List<Worker> workers;

	@OneToMany(mappedBy = "project")
	private List<Review> reviews;

	public void setId(Long id) {
		this.id = id;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public void setStatus(ProjectStatus status) {
		this.status = status;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public void setMaterialCost(BigDecimal materialCost) {
		this.materialCost = materialCost;
	}

	public void setLaborCost(BigDecimal laborCost) {
		this.laborCost = laborCost;
	}

	public void setScheduledHours(Integer scheduledHours) {
		this.scheduledHours = scheduledHours;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setWorkers(List<Worker> workers) {
		this.workers = workers;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
}
