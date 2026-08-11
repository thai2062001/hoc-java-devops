package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.EmployeeDto;
import com.example.demodevops.dto.EmployeeShiftDto;
import com.example.demodevops.service.EmployeeShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employee-shifts")
public class EmployeeShiftController {

    private final EmployeeShiftService employeeShiftService;

    @Autowired
    public EmployeeShiftController(EmployeeShiftService employeeShiftService) {
        this.employeeShiftService = employeeShiftService;
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<EmployeeShiftDto>>> getSchedulesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<EmployeeShiftDto> schedules = employeeShiftService.getSchedulesByDate(date);
        return ResponseEntity.ok(ApiResponse.success(schedules, "Retrieved schedules successfully"));
    }

    @GetMapping("/employee/{employeeId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<List<EmployeeShiftDto>>> getSchedulesByEmployeeAndDate(
            @PathVariable Long employeeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<EmployeeShiftDto> schedules = employeeShiftService.getSchedulesByEmployeeAndDate(employeeId, date);
        return ResponseEntity.ok(ApiResponse.success(schedules, "Retrieved employee schedules successfully"));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<EmployeeShiftDto>> assignShift(@RequestBody EmployeeShiftDto dto) {
        EmployeeShiftDto assigned = employeeShiftService.assignShift(dto);
        return ResponseEntity.ok(ApiResponse.success(assigned, "Assigned shift successfully"));
    }

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<EmployeeShiftDto>> checkIn(
            @RequestParam Long employeeId,
            @RequestParam Long shiftId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        EmployeeShiftDto updated = employeeShiftService.checkIn(employeeId, shiftId, date);
        return ResponseEntity.ok(ApiResponse.success(updated, "Checked in successfully"));
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<EmployeeShiftDto>> checkOut(
            @RequestParam Long employeeId,
            @RequestParam Long shiftId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String note) {
        EmployeeShiftDto updated = employeeShiftService.checkOut(employeeId, shiftId, date, note);
        return ResponseEntity.ok(ApiResponse.success(updated, "Checked out successfully"));
    }

    @GetMapping("/available-technicians/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getAvailableTechnicians(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<EmployeeDto> technicians = employeeShiftService.getAvailableTechnicians(date);
        return ResponseEntity.ok(ApiResponse.success(technicians, "Retrieved available technicians successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long id) {
        employeeShiftService.deleteSchedule(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted schedule successfully"));
    }
}
