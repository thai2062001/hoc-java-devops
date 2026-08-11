package com.example.demodevops.service;

import com.example.demodevops.dto.EmployeeDto;
import com.example.demodevops.dto.EmployeeShiftDto;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeShiftService {
    List<EmployeeShiftDto> getSchedulesByDate(LocalDate date);
    List<EmployeeShiftDto> getSchedulesByEmployeeAndDate(Long employeeId, LocalDate date);
    EmployeeShiftDto assignShift(EmployeeShiftDto dto);
    EmployeeShiftDto checkIn(Long employeeId, Long shiftId, LocalDate date);
    EmployeeShiftDto checkOut(Long employeeId, Long shiftId, LocalDate date, String note);
    List<EmployeeDto> getAvailableTechnicians(LocalDate date);
    void deleteSchedule(Long id);
}
