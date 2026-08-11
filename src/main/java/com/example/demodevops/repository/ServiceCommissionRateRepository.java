package com.example.demodevops.repository;

import com.example.demodevops.model.ServiceCommissionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceCommissionRateRepository extends JpaRepository<ServiceCommissionRate, Long> {
    Optional<ServiceCommissionRate> findByServiceIdAndEmployeeId(Long serviceId, Long employeeId);
}
