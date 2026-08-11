package com.example.demodevops.repository;

import com.example.demodevops.model.Appointment;
import com.example.demodevops.model.Appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByCustomerId(Long customerId);
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);
    List<Appointment> findByPrimaryEmployeeIdAndAppointmentDateAndStatusNot(Long employeeId, LocalDate date, AppointmentStatus status);
}
