package com.example.demodevops.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TreatmentHistoryDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long appointmentServiceId;
    private Long employeeId;
    private String employeeName;
    private LocalDate treatmentDate;
    private String notes;
    private String beforeImageUrl;
    private String afterImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TreatmentHistoryDto() {}

    public TreatmentHistoryDto(Long id, Long customerId, String customerName, Long appointmentServiceId,
                               Long employeeId, String employeeName, LocalDate treatmentDate, String notes,
                               String beforeImageUrl, String afterImageUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.appointmentServiceId = appointmentServiceId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.treatmentDate = treatmentDate;
        this.notes = notes;
        this.beforeImageUrl = beforeImageUrl;
        this.afterImageUrl = afterImageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Long getAppointmentServiceId() { return appointmentServiceId; }
    public void setAppointmentServiceId(Long appointmentServiceId) { this.appointmentServiceId = appointmentServiceId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public LocalDate getTreatmentDate() { return treatmentDate; }
    public void setTreatmentDate(LocalDate treatmentDate) { this.treatmentDate = treatmentDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getBeforeImageUrl() { return beforeImageUrl; }
    public void setBeforeImageUrl(String beforeImageUrl) { this.beforeImageUrl = beforeImageUrl; }

    public String getAfterImageUrl() { return afterImageUrl; }
    public void setAfterImageUrl(String afterImageUrl) { this.afterImageUrl = afterImageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
