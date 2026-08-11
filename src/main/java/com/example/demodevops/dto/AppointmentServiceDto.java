package com.example.demodevops.dto;

import com.example.demodevops.model.AppointmentService.AppointmentServiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AppointmentServiceDto {
    private Long id;
    private Long appointmentId;
    private Long serviceId;
    private String serviceName;
    private Long employeeId;
    private String employeeName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private AppointmentServiceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppointmentServiceDto() {}

    public AppointmentServiceDto(Long id, Long appointmentId, Long serviceId, String serviceName, Long employeeId,
                                 String employeeName, Integer quantity, BigDecimal unitPrice,
                                 AppointmentServiceStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public AppointmentServiceStatus getStatus() { return status; }
    public void setStatus(AppointmentServiceStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
