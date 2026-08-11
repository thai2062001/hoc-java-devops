package com.example.demodevops.service;

import com.example.demodevops.dto.EmployeeDto;
import com.example.demodevops.dto.EmployeeSaveDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.Employee;
import com.example.demodevops.model.Role;
import com.example.demodevops.repository.EmployeeRepository;
import com.example.demodevops.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return convertToDto(employee);
    }

    @Override
    public EmployeeDto createEmployee(EmployeeSaveDto employeeSaveDto) {
        Role role = roleRepository.findById(employeeSaveDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + employeeSaveDto.getRoleId()));

        Employee employee = new Employee();
        employee.setRole(role);
        employee.setEmployeeCode(employeeSaveDto.getEmployeeCode());
        employee.setFullName(employeeSaveDto.getFullName());
        employee.setEmail(employeeSaveDto.getEmail());
        employee.setPhone(employeeSaveDto.getPhone());
        employee.setAvatarUrl(employeeSaveDto.getAvatarUrl());
        employee.setGender(employeeSaveDto.getGender());
        employee.setDob(employeeSaveDto.getDob());
        employee.setHireDate(employeeSaveDto.getHireDate());
        employee.setBaseSalary(employeeSaveDto.getBaseSalary());
        employee.setStatus(employeeSaveDto.getStatus() != null ? employeeSaveDto.getStatus() : Employee.EmployeeStatus.ACTIVE);

        // Mã hoá mật khẩu bằng BCrypt
        String encryptedPassword = passwordEncoder.encode(employeeSaveDto.getPassword());
        employee.setPasswordHash(encryptedPassword);

        Employee saved = employeeRepository.save(employee);
        return convertToDto(saved);
    }

    @Override
    public EmployeeDto updateEmployee(Long id, EmployeeSaveDto employeeSaveDto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        Role role = roleRepository.findById(employeeSaveDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + employeeSaveDto.getRoleId()));

        employee.setRole(role);
        employee.setEmployeeCode(employeeSaveDto.getEmployeeCode());
        employee.setFullName(employeeSaveDto.getFullName());
        employee.setEmail(employeeSaveDto.getEmail());
        employee.setPhone(employeeSaveDto.getPhone());
        employee.setAvatarUrl(employeeSaveDto.getAvatarUrl());
        employee.setGender(employeeSaveDto.getGender());
        employee.setDob(employeeSaveDto.getDob());
        employee.setHireDate(employeeSaveDto.getHireDate());
        employee.setBaseSalary(employeeSaveDto.getBaseSalary());
        if (employeeSaveDto.getStatus() != null) {
            employee.setStatus(employeeSaveDto.getStatus());
        }

        // Cập nhật mật khẩu nếu có truyền lên mật khẩu mới
        if (employeeSaveDto.getPassword() != null && !employeeSaveDto.getPassword().trim().isEmpty()) {
            employee.setPasswordHash(passwordEncoder.encode(employeeSaveDto.getPassword()));
        }

        Employee updated = employeeRepository.save(employee);
        return convertToDto(updated);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }

    private EmployeeDto convertToDto(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getRole().getId(),
                employee.getRole().getName(),
                employee.getRole().getCode(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getAvatarUrl(),
                employee.getGender(),
                employee.getDob(),
                employee.getHireDate(),
                employee.getBaseSalary(),
                employee.getStatus(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}
