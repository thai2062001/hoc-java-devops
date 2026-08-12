package com.example.demodevops.dto;

import com.example.demodevops.model.EmployeeShift.ShiftStatus;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeShiftDto {
    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    private String employeeName;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;
    private String shiftName;

    @NotNull(message = "Work date is required")
    private LocalDate workDate;
    private ShiftStatus status;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeShiftDto() {}

    public EmployeeShiftDto(Long id, Long employeeId, String employeeName, Long shiftId, String shiftName,
                            LocalDate workDate, ShiftStatus status, LocalDateTime checkInTime,
                            LocalDateTime checkOutTime, String note, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.workDate = workDate;
        this.status = status;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Long getShiftId() { return shiftId; }
    public void setShiftId(Long shiftId) { this.shiftId = shiftId; }

    public String getShiftName() { return shiftName; }
    public void setShiftName(String shiftName) { this.shiftName = shiftName; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public ShiftStatus getStatus() { return status; }
    public void setStatus(ShiftStatus status) { this.status = status; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
