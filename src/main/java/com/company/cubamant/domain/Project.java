package com.company.cubamant.domain;
import jakarta.persistence.*;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "project")
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

	private Integer scheduledHours;

	@ManyToOne(fetch = FetchType.LAZY) // ✅ FIX
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToMany(fetch = FetchType.LAZY) // ✅ FIX
	@JoinTable(
			name = "project_workers",
			joinColumns = @JoinColumn(name = "project_id"),
			inverseJoinColumns = @JoinColumn(name = "worker_id")
	)
	private List<Worker> workers;

	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY) // ✅ FIX
	private List<Review> reviews;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public ProjectStatus getStatus() {
		return status;
	}

	public void setStatus(ProjectStatus status) {
		this.status = status;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public BigDecimal getMaterialCost() {
		return materialCost;
	}

	public void setMaterialCost(BigDecimal materialCost) {
		this.materialCost = materialCost;
	}

	public BigDecimal getLaborCost() {
		return laborCost;
	}

	public void setLaborCost(BigDecimal laborCost) {
		this.laborCost = laborCost;
	}

	public Integer getScheduledHours() {
		return scheduledHours;
	}

	public void setScheduledHours(Integer scheduledHours) {
		this.scheduledHours = scheduledHours;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Worker> getWorkers() {
		return workers;
	}

	public void setWorkers(List<Worker> workers) {
		this.workers = workers;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
}
