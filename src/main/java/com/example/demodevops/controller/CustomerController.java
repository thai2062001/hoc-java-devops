package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.CustomerDto;
import com.example.demodevops.dto.CustomerSaveDto;
import com.example.demodevops.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success(customers, "Retrieved all customers successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable Long id) {
        CustomerDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(customer, "Retrieved customer details successfully"));
    }

    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerByPhone(@PathVariable String phone) {
        CustomerDto customer = customerService.getCustomerByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(customer, "Retrieved customer details successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(@Valid @RequestBody CustomerSaveDto customerSaveDto) {
        CustomerDto created = customerService.createCustomer(customerSaveDto);
        return new ResponseEntity<>(ApiResponse.success(created, "Customer created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerSaveDto customerSaveDto) {
        CustomerDto updated = customerService.updateCustomer(id, customerSaveDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Customer details updated successfully"));
    }

    @PutMapping("/{id}/points")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<CustomerDto>> updateLoyaltyPoints(
            @PathVariable Long id, 
            @RequestParam Integer pointsChange) {
        CustomerDto updated = customerService.updateLoyaltyPoints(id, pointsChange);
        return ResponseEntity.ok(ApiResponse.success(updated, "Customer loyalty points updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Customer profile deleted successfully"));
    }
}
