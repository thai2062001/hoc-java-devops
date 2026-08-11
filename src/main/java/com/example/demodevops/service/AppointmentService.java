package com.example.demodevops.service;

import com.example.demodevops.dto.AppointmentDto;
import com.example.demodevops.dto.BookingRequestDto;
import com.example.demodevops.model.Appointment.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    List<AppointmentDto> getAllAppointments();
    AppointmentDto getAppointmentById(Long id);
    List<AppointmentDto> getAppointmentsByCustomer(Long customerId);
    List<AppointmentDto> getAppointmentsByDate(LocalDate date);
    AppointmentDto createBooking(BookingRequestDto bookingRequestDto, Long creatorId);
    AppointmentDto updateAppointmentStatus(Long id, AppointmentStatus status, String cancelledReason);
    boolean isTechnicianAvailable(Long employeeId, LocalDateTime startTime, LocalDateTime endTime);
}
