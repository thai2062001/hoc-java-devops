package com.example.demodevops.repository;

import com.example.demodevops.model.Appointment.AppointmentStatus;
import com.example.demodevops.model.AppointmentService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Long> {
    List<AppointmentService> findByAppointmentId(Long appointmentId);
    List<AppointmentService> findByEmployeeIdAndAppointmentAppointmentDateAndAppointmentStatusNot(
            Long employeeId, LocalDate date, AppointmentStatus status);
}
