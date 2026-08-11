package com.example.demodevops.service;

import com.example.demodevops.dto.EmployeeDto;
import com.example.demodevops.dto.EmployeeSaveDto;
import java.util.List;

public interface EmployeeService {
    List<EmployeeDto> getAllEmployees();
    EmployeeDto getEmployeeById(Long id);
    EmployeeDto createEmployee(EmployeeSaveDto employeeSaveDto);
    EmployeeDto updateEmployee(Long id, EmployeeSaveDto employeeSaveDto);
    void deleteEmployee(Long id);
}
