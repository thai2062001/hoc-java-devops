package com.example.demodevops.service;

import com.example.demodevops.dto.AppointmentDto;
import com.example.demodevops.dto.AppointmentServiceDto;
import com.example.demodevops.dto.BookingRequestDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.*;
import com.example.demodevops.model.Appointment.AppointmentStatus;
import com.example.demodevops.model.AppointmentService.AppointmentServiceStatus;
import com.example.demodevops.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeeShiftRepository employeeShiftRepository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  AppointmentServiceRepository appointmentServiceRepository,
                                  CustomerRepository customerRepository,
                                  EmployeeRepository employeeRepository,
                                  ServiceRepository serviceRepository,
                                  EmployeeShiftRepository employeeShiftRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.serviceRepository = serviceRepository;
        this.employeeShiftRepository = employeeShiftRepository;
    }

    @Override
    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        return convertToDto(appointment);
    }

    @Override
    public List<AppointmentDto> getAppointmentsByCustomer(Long customerId) {
        return appointmentRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDto createBooking(BookingRequestDto dto, Long creatorId) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        Employee creator = creatorId != null ? employeeRepository.findById(creatorId).orElse(null) : null;
        Employee primaryEmployee = dto.getPrimaryEmployeeId() != null ? 
                employeeRepository.findById(dto.getPrimaryEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + dto.getPrimaryEmployeeId())) : null;

        LocalDate workDate = dto.getStartTime().toLocalDate();

        // 1. Tính toán tổng thời lượng dịch vụ và lấy thông tin chi tiết dịch vụ
        int totalDurationMinutes = 0;
        List<com.example.demodevops.model.Service> spaServices = new ArrayList<>();
        for (BookingRequestDto.BookedServiceDto serviceDto : dto.getServices()) {
            com.example.demodevops.model.Service svc = serviceRepository.findById(serviceDto.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceDto.getServiceId()));
            spaServices.add(svc);
            totalDurationMinutes += svc.getDurationMinutes();
        }

        LocalDateTime startTime = dto.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(totalDurationMinutes);

        // 2. Xác thực ca trực & lịch trùng của Kỹ thuật viên chính (nếu chỉ định)
        if (primaryEmployee != null) {
            validateTechnicianAvailability(primaryEmployee, startTime, endTime, workDate);
        }

        // 3. Khởi tạo thực thể Appointment
        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setPrimaryEmployee(primaryEmployee);
        appointment.setCreatedBy(creator);
        appointment.setAppointmentDate(workDate);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setNote(dto.getNote());
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setSource(dto.getSource());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // 4. Khởi tạo danh sách các dịch vụ đi kèm trong buổi hẹn (AppointmentService)
        List<com.example.demodevops.model.AppointmentService> savedDetails = new ArrayList<>();
        for (int i = 0; i < dto.getServices().size(); i++) {
            BookingRequestDto.BookedServiceDto detailDto = dto.getServices().get(i);
            com.example.demodevops.model.Service svc = spaServices.get(i);

            Employee tech = detailDto.getEmployeeId() != null ? 
                    employeeRepository.findById(detailDto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + detailDto.getEmployeeId())) : primaryEmployee;

            // Xác thực tính khả dụng của kỹ thuật viên phụ trách dịch vụ này
            if (tech != null && !tech.equals(primaryEmployee)) {
                validateTechnicianAvailability(tech, startTime, endTime, workDate);
            }

            com.example.demodevops.model.AppointmentService detail = new com.example.demodevops.model.AppointmentService();
            detail.setAppointment(savedAppointment);
            detail.setService(svc);
            detail.setEmployee(tech);
            detail.setQuantity(1);
            detail.setUnitPrice(svc.getPrice());
            detail.setStatus(AppointmentServiceStatus.PENDING);

            savedDetails.add(appointmentServiceRepository.save(detail));
        }

        return convertToDto(savedAppointment, savedDetails);
    }

    @Override
    public AppointmentDto updateAppointmentStatus(Long id, AppointmentStatus status, String cancelledReason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        appointment.setStatus(status);
        if (status == AppointmentStatus.CANCELLED) {
            appointment.setCancelledReason(cancelledReason);
            // Hủy toàn bộ dịch vụ đi kèm
            List<com.example.demodevops.model.AppointmentService> details = appointmentServiceRepository.findByAppointmentId(id);
            for (com.example.demodevops.model.AppointmentService detail : details) {
                detail.setStatus(AppointmentServiceStatus.CANCELLED);
                appointmentServiceRepository.save(detail);
            }
        } else if (status == AppointmentStatus.COMPLETED) {
            // Hoàn tất toàn bộ dịch vụ đi kèm
            List<com.example.demodevops.model.AppointmentService> details = appointmentServiceRepository.findByAppointmentId(id);
            for (com.example.demodevops.model.AppointmentService detail : details) {
                detail.setStatus(AppointmentServiceStatus.DONE);
                appointmentServiceRepository.save(detail);
            }
        }

        Appointment updated = appointmentRepository.save(appointment);
        return convertToDto(updated);
    }

    @Override
    public boolean isTechnicianAvailable(Long employeeId, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDate date = startTime.toLocalDate();

        // 1. Kiểm tra xem kỹ thuật viên có ca trực trong ngày hay không
        List<EmployeeShift> shifts = employeeShiftRepository.findByEmployeeIdAndWorkDate(employeeId, date);
        boolean onShift = false;
        for (EmployeeShift es : shifts) {
            if (es.getStatus() == EmployeeShift.ShiftStatus.ASSIGNED || es.getStatus() == EmployeeShift.ShiftStatus.CHECKED_IN) {
                onShift = true;
                break;
            }
        }
        if (!onShift) {
            return false;
        }

        // 2. Kiểm tra xem có trùng lịch hẹn nào khác đang hoạt động hay không
        List<Appointment> primaryBookings = appointmentRepository
                .findByPrimaryEmployeeIdAndAppointmentDateAndStatusNot(employeeId, date, AppointmentStatus.CANCELLED);
        for (Appointment appt : primaryBookings) {
            if (isOverlapping(startTime, endTime, appt.getStartTime(), appt.getEndTime())) {
                return false;
            }
        }

        // 3. Kiểm tra xem có trùng lịch phân dịch vụ cụ thể nào không
        List<com.example.demodevops.model.AppointmentService> serviceBookings = appointmentServiceRepository
                .findByEmployeeIdAndAppointmentAppointmentDateAndAppointmentStatusNot(employeeId, date, AppointmentStatus.CANCELLED);
        for (com.example.demodevops.model.AppointmentService detail : serviceBookings) {
            if (isOverlapping(startTime, endTime, detail.getAppointment().getStartTime(), detail.getAppointment().getEndTime())) {
                return false;
            }
        }

        return true;
    }

    private void validateTechnicianAvailability(Employee technician, LocalDateTime startTime, LocalDateTime endTime, LocalDate workDate) {
        // Kiểm tra ca trực
        List<EmployeeShift> shifts = employeeShiftRepository.findByEmployeeIdAndWorkDate(technician.getId(), workDate);
        boolean hasShift = shifts.stream().anyMatch(es -> 
                es.getStatus() == EmployeeShift.ShiftStatus.ASSIGNED || es.getStatus() == EmployeeShift.ShiftStatus.CHECKED_IN);
        if (!hasShift) {
            throw new IllegalArgumentException("Kỹ thuật viên " + technician.getFullName() + " không có lịch trực vào ngày " + workDate);
        }

        // Kiểm tra lịch trùng
        if (!isTechnicianAvailable(technician.getId(), startTime, endTime)) {
            throw new IllegalArgumentException("Kỹ thuật viên " + technician.getFullName() + " đã có lịch hẹn trùng từ " 
                    + startTime.toLocalTime() + " đến " + endTime.toLocalTime());
        }
    }

    private boolean isOverlapping(LocalDateTime startA, LocalDateTime endA, LocalDateTime startB, LocalDateTime endB) {
        return startA.isBefore(endB) && startB.isBefore(endA);
    }

    private AppointmentDto convertToDto(Appointment appt) {
        List<com.example.demodevops.model.AppointmentService> details = appointmentServiceRepository.findByAppointmentId(appt.getId());
        return convertToDto(appt, details);
    }

    private AppointmentDto convertToDto(Appointment appt, List<com.example.demodevops.model.AppointmentService> details) {
        List<AppointmentServiceDto> serviceDtos = details.stream()
                .map(d -> new AppointmentServiceDto(
                        d.getId(),
                        d.getAppointment().getId(),
                        d.getService().getId(),
                        d.getService().getName(),
                        d.getEmployee() != null ? d.getEmployee().getId() : null,
                        d.getEmployee() != null ? d.getEmployee().getFullName() : "Chưa phân công",
                        d.getQuantity(),
                        d.getUnitPrice(),
                        d.getStatus(),
                        d.getCreatedAt(),
                        d.getUpdatedAt()
                )).collect(Collectors.toList());

        return new AppointmentDto(
                appt.getId(),
                appt.getCustomer().getId(),
                appt.getCustomer().getFullName(),
                appt.getCustomer().getPhone(),
                appt.getPrimaryEmployee() != null ? appt.getPrimaryEmployee().getId() : null,
                appt.getPrimaryEmployee() != null ? appt.getPrimaryEmployee().getFullName() : "Chưa phân công",
                appt.getAppointmentDate(),
                appt.getStartTime(),
                appt.getEndTime(),
                appt.getStatus(),
                appt.getSource(),
                appt.getNote(),
                appt.getCancelledReason(),
                serviceDtos,
                appt.getCreatedAt(),
                appt.getUpdatedAt()
        );
    }
}
