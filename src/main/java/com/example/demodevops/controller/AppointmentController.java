package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.AppointmentDto;
import com.example.demodevops.dto.BookingRequestDto;
import com.example.demodevops.model.Appointment.AppointmentStatus;
import com.example.demodevops.security.EmployeePrincipal;
import com.example.demodevops.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAllAppointments() {
        List<AppointmentDto> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(ApiResponse.success(appointments, "Retrieved all appointments successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<AppointmentDto>> getAppointmentById(@PathVariable Long id) {
        AppointmentDto appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(ApiResponse.success(appointment, "Retrieved appointment details successfully"));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAppointmentsByCustomer(@PathVariable Long customerId) {
        List<AppointmentDto> appointments = appointmentService.getAppointmentsByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success(appointments, "Retrieved customer appointments successfully"));
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AppointmentDto> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(ApiResponse.success(appointments, "Retrieved appointments for date successfully"));
    }

    @PostMapping("/book")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentDto>> createBooking(
            @RequestBody BookingRequestDto bookingRequestDto,
            @AuthenticationPrincipal EmployeePrincipal principal) {
        
        Long creatorId = principal != null ? principal.getEmployee().getId() : null;
        AppointmentDto created = appointmentService.createBooking(bookingRequestDto, creatorId);
        return new ResponseEntity<>(ApiResponse.success(created, "Appointment booked successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentDto>> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status,
            @RequestParam(required = false) String cancelledReason) {
        AppointmentDto updated = appointmentService.updateAppointmentStatus(id, status, cancelledReason);
        return ResponseEntity.ok(ApiResponse.success(updated, "Appointment status updated successfully"));
    }

    @GetMapping("/check-availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<Boolean>> checkTechnicianAvailability(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        boolean available = appointmentService.isTechnicianAvailable(employeeId, startTime, endTime);
        return ResponseEntity.ok(ApiResponse.success(available, "Technician availability check complete"));
    }
}
