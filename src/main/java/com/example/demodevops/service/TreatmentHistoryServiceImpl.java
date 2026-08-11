package com.example.demodevops.service;

import com.example.demodevops.dto.TreatmentHistoryDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.Customer;
import com.example.demodevops.model.Employee;
import com.example.demodevops.model.TreatmentHistory;
import com.example.demodevops.model.AppointmentService;
import com.example.demodevops.repository.CustomerRepository;
import com.example.demodevops.repository.EmployeeRepository;
import com.example.demodevops.repository.TreatmentHistoryRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TreatmentHistoryServiceImpl implements TreatmentHistoryService {

    private final TreatmentHistoryRepository historyRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;

    @Autowired
    public TreatmentHistoryServiceImpl(TreatmentHistoryRepository historyRepository,
                                       CustomerRepository customerRepository,
                                       EmployeeRepository employeeRepository,
                                       EntityManager entityManager) {
        this.historyRepository = historyRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<TreatmentHistoryDto> getHistoryByCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return historyRepository.findByCustomerIdOrderByTreatmentDateDesc(customerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TreatmentHistoryDto getHistoryById(Long id) {
        TreatmentHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment record not found with id: " + id));
        return convertToDto(history);
    }

    @Override
    public TreatmentHistoryDto addHistoryRecord(TreatmentHistoryDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));

        TreatmentHistory history = new TreatmentHistory();
        history.setCustomer(customer);
        history.setEmployee(employee);
        history.setTreatmentDate(dto.getTreatmentDate());
        history.setNotes(dto.getNotes());
        history.setBeforeImageUrl(dto.getBeforeImageUrl());
        history.setAfterImageUrl(dto.getAfterImageUrl());

        // appointmentServiceId là tuỳ chọn
        if (dto.getAppointmentServiceId() != null) {
            history.setAppointmentService(entityManager.getReference(AppointmentService.class, dto.getAppointmentServiceId()));
        }

        TreatmentHistory saved = historyRepository.save(history);
        return convertToDto(saved);
    }

    @Override
    public TreatmentHistoryDto updateHistoryRecord(Long id, TreatmentHistoryDto dto) {
        TreatmentHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment record not found with id: " + id));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));

        history.setCustomer(customer);
        history.setEmployee(employee);
        history.setTreatmentDate(dto.getTreatmentDate());
        history.setNotes(dto.getNotes());
        history.setBeforeImageUrl(dto.getBeforeImageUrl());
        history.setAfterImageUrl(dto.getAfterImageUrl());
        
        if (dto.getAppointmentServiceId() != null) {
            history.setAppointmentService(entityManager.getReference(AppointmentService.class, dto.getAppointmentServiceId()));
        } else {
            history.setAppointmentService(null);
        }

        TreatmentHistory updated = historyRepository.save(history);
        return convertToDto(updated);
    }

    @Override
    public void deleteHistoryRecord(Long id) {
        TreatmentHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment record not found with id: " + id));
        historyRepository.delete(history);
    }

    private TreatmentHistoryDto convertToDto(TreatmentHistory th) {
        return new TreatmentHistoryDto(
                th.getId(),
                th.getCustomer().getId(),
                th.getCustomer().getFullName(),
                th.getAppointmentService() != null ? th.getAppointmentService().getId() : null,
                th.getEmployee().getId(),
                th.getEmployee().getFullName(),
                th.getTreatmentDate(),
                th.getNotes(),
                th.getBeforeImageUrl(),
                th.getAfterImageUrl(),
                th.getCreatedAt(),
                th.getUpdatedAt()
        );
    }
}
