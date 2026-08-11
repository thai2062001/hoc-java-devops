package com.example.demodevops.repository;

import com.example.demodevops.model.TreatmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentHistoryRepository extends JpaRepository<TreatmentHistory, Long> {
    List<TreatmentHistory> findByCustomerIdOrderByTreatmentDateDesc(Long customerId);
}
