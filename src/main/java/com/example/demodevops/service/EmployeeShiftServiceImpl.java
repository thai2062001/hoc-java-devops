package com.example.demodevops.service;

import com.example.demodevops.dto.EmployeeDto;
import com.example.demodevops.dto.EmployeeShiftDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.Employee;
import com.example.demodevops.model.EmployeeShift;
import com.example.demodevops.model.EmployeeShift.ShiftStatus;
import com.example.demodevops.model.Shift;
import com.example.demodevops.repository.EmployeeRepository;
import com.example.demodevops.repository.EmployeeShiftRepository;
import com.example.demodevops.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeShiftServiceImpl implements EmployeeShiftService {

    private final EmployeeShiftRepository employeeShiftRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;

    @Autowired
    public EmployeeShiftServiceImpl(EmployeeShiftRepository employeeShiftRepository,
                                   EmployeeRepository employeeRepository,
                                   ShiftRepository shiftRepository) {
        this.employeeShiftRepository = employeeShiftRepository;
        this.employeeRepository = employeeRepository;
        this.shiftRepository = shiftRepository;
    }

    @Override
    public List<EmployeeShiftDto> getSchedulesByDate(LocalDate date) {
        return employeeShiftRepository.findByWorkDate(date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeShiftDto> getSchedulesByEmployeeAndDate(Long employeeId, LocalDate date) {
        return employeeShiftRepository.findByEmployeeIdAndWorkDate(employeeId, date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeShiftDto assignShift(EmployeeShiftDto dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));

        Shift shift = shiftRepository.findById(dto.getShiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + dto.getShiftId()));

        // Kiểm tra xem đã tồn tại phân ca này chưa
        EmployeeShift employeeShift = employeeShiftRepository
                .findByEmployeeIdAndShiftIdAndWorkDate(dto.getEmployeeId(), dto.getShiftId(), dto.getWorkDate())
                .orElse(null);

        if (employeeShift == null) {
            employeeShift = new EmployeeShift();
            employeeShift.setEmployee(employee);
            employeeShift.setShift(shift);
            employeeShift.setWorkDate(dto.getWorkDate());
        }

        employeeShift.setStatus(dto.getStatus() != null ? dto.getStatus() : ShiftStatus.ASSIGNED);
        employeeShift.setNote(dto.getNote());

        EmployeeShift saved = employeeShiftRepository.save(employeeShift);
        return convertToDto(saved);
    }

    @Override
    public EmployeeShiftDto checkIn(Long employeeId, Long shiftId, LocalDate date) {
        EmployeeShift employeeShift = employeeShiftRepository
                .findByEmployeeIdAndShiftIdAndWorkDate(employeeId, shiftId, date)
                .orElseThrow(() -> new ResourceNotFoundException("No shift schedule found for employee " 
                        + employeeId + " and shift " + shiftId + " on date " + date));

        employeeShift.setStatus(ShiftStatus.CHECKED_IN);
        employeeShift.setCheckInTime(LocalDateTime.now());
        EmployeeShift updated = employeeShiftRepository.save(employeeShift);
        return convertToDto(updated);
    }

    @Override
    public EmployeeShiftDto checkOut(Long employeeId, Long shiftId, LocalDate date, String note) {
        EmployeeShift employeeShift = employeeShiftRepository
                .findByEmployeeIdAndShiftIdAndWorkDate(employeeId, shiftId, date)
                .orElseThrow(() -> new ResourceNotFoundException("No shift schedule found for employee " 
                        + employeeId + " and shift " + shiftId + " on date " + date));

        employeeShift.setStatus(ShiftStatus.CHECKED_OUT);
        employeeShift.setCheckOutTime(LocalDateTime.now());
        if (note != null) {
            employeeShift.setNote(note);
        }
        EmployeeShift updated = employeeShiftRepository.save(employeeShift);
        return convertToDto(updated);
    }

    @Override
    public List<EmployeeDto> getAvailableTechnicians(LocalDate date) {
        return employeeShiftRepository.findAvailableTechniciansByDate(date).stream()
                .map(es -> convertEmployeeToDto(es.getEmployee()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSchedule(Long id) {
        EmployeeShift employeeShift = employeeShiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift schedule not found with id: " + id));
        employeeShiftRepository.delete(employeeShift);
    }

    private EmployeeShiftDto convertToDto(EmployeeShift es) {
        return new EmployeeShiftDto(
                es.getId(),
                es.getEmployee().getId(),
                es.getEmployee().getFullName(),
                es.getShift().getId(),
                es.getShift().getName(),
                es.getWorkDate(),
                es.getStatus(),
                es.getCheckInTime(),
                es.getCheckOutTime(),
                es.getNote(),
                es.getCreatedAt(),
                es.getUpdatedAt()
        );
    }

    private EmployeeDto convertEmployeeToDto(Employee e) {
        return new EmployeeDto(
                e.getId(),
                e.getRole().getId(),
                e.getRole().getName(),
                e.getRole().getCode(),
                e.getEmployeeCode(),
                e.getFullName(),
                e.getEmail(),
                e.getPhone(),
                e.getAvatarUrl(),
                e.getGender(),
                e.getDob(),
                e.getHireDate(),
                e.getBaseSalary(),
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
