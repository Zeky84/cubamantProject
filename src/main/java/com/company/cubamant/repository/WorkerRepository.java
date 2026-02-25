package com.company.cubamant.repository;

import com.company.cubamant.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
}
