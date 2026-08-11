package com.example.demodevops.security;

import com.example.demodevops.model.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class EmployeePrincipal implements UserDetails {

    private final Employee employee;

    public EmployeePrincipal(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (employee.getRole() == null || employee.getRole().getCode() == null) {
            return Collections.emptyList();
        }
        // Thêm tiền tố ROLE_ để hoạt động đúng với Spring Security phân quyền qua role
        String roleName = "ROLE_" + employee.getRole().getCode().toUpperCase();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return employee.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return employee.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return employee.getStatus() != Employee.EmployeeStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return employee.getStatus() == Employee.EmployeeStatus.ACTIVE;
    }
}
