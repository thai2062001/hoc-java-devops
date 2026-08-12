package com.example.demodevops.dto;

import com.example.demodevops.model.Appointment.AppointmentSource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class BookingRequestDto {
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    private Long primaryEmployeeId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    private AppointmentSource source = AppointmentSource.OFFLINE;
    private String note;

    @NotEmpty(message = "At least one service must be selected")
    @Valid
    private List<BookedServiceDto> services;

    public BookingRequestDto() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getPrimaryEmployeeId() { return primaryEmployeeId; }
    public void setPrimaryEmployeeId(Long primaryEmployeeId) { this.primaryEmployeeId = primaryEmployeeId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public AppointmentSource getSource() { return source; }
    public void setSource(AppointmentSource source) { this.source = source; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public List<BookedServiceDto> getServices() { return services; }
    public void setServices(List<BookedServiceDto> services) { this.services = services; }

    public static class BookedServiceDto {
        @NotNull(message = "Service ID is required")
        private Long serviceId;
        private Long employeeId;

        public BookedServiceDto() {}

        public Long getServiceId() { return serviceId; }
        public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    }
}
