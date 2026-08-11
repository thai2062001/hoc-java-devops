package com.example.demodevops.dto;

import com.example.demodevops.model.Appointment.AppointmentSource;
import com.example.demodevops.model.Appointment.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long primaryEmployeeId;
    private String primaryEmployeeName;
    private LocalDate appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
    private AppointmentSource source;
    private String note;
    private String cancelledReason;
    private List<AppointmentServiceDto> services;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppointmentDto() {}

    public AppointmentDto(Long id, Long customerId, String customerName, String customerPhone, Long primaryEmployeeId,
                          String primaryEmployeeName, LocalDate appointmentDate, LocalDateTime startTime,
                          LocalDateTime endTime, AppointmentStatus status, AppointmentSource source, String note,
                          String cancelledReason, List<AppointmentServiceDto> services, LocalDateTime createdAt,
                          LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.primaryEmployeeId = primaryEmployeeId;
        this.primaryEmployeeName = primaryEmployeeName;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.source = source;
        this.note = note;
        this.cancelledReason = cancelledReason;
        this.services = services;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Long getPrimaryEmployeeId() { return primaryEmployeeId; }
    public void setPrimaryEmployeeId(Long primaryEmployeeId) { this.primaryEmployeeId = primaryEmployeeId; }

    public String getPrimaryEmployeeName() { return primaryEmployeeName; }
    public void setPrimaryEmployeeName(String primaryEmployeeName) { this.primaryEmployeeName = primaryEmployeeName; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public AppointmentSource getSource() { return source; }
    public void setSource(AppointmentSource source) { this.source = source; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getCancelledReason() { return cancelledReason; }
    public void setCancelledReason(String cancelledReason) { this.cancelledReason = cancelledReason; }

    public List<AppointmentServiceDto> getServices() { return services; }
    public void setServices(List<AppointmentServiceDto> services) { this.services = services; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
