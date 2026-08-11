package com.example.demodevops.repository;

import com.example.demodevops.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByCustomerId(Long customerId);
    Optional<Invoice> findByAppointmentId(Long appointmentId);
    Optional<Invoice> findByInvoiceNo(String invoiceNo);
}
