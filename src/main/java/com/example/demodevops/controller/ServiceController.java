package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.ServiceDto;
import com.example.demodevops.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceDto>>> getAllServices() {
        List<ServiceDto> services = serviceService.getAllServices();
        return ResponseEntity.ok(ApiResponse.success(services, "Retrieved all services successfully"));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ServiceDto>>> getServicesByCategoryId(@PathVariable Long categoryId) {
        List<ServiceDto> services = serviceService.getServicesByCategoryId(categoryId);
        return ResponseEntity.ok(ApiResponse.success(services, "Retrieved services by category successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceDto>> getServiceById(@PathVariable Long id) {
        ServiceDto serviceDto = serviceService.getServiceById(id);
        return ResponseEntity.ok(ApiResponse.success(serviceDto, "Retrieved service successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceDto>> createService(@Valid @RequestBody ServiceDto serviceDto) {
        ServiceDto created = serviceService.createService(serviceDto);
        return new ResponseEntity<>(ApiResponse.success(created, "Created service successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceDto>> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDto serviceDto) {
        ServiceDto updated = serviceService.updateService(id, serviceDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Updated service successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted service successfully"));
    }
}
