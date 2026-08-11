package com.example.demodevops.repository;

import com.example.demodevops.model.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByEmployeeId(Long employeeId);
    List<Commission> findByInvoiceId(Long invoiceId);
}
