package com.example.demodevops.service;

import com.example.demodevops.dto.ServiceDto;
import java.util.List;

public interface ServiceService {
    List<ServiceDto> getAllServices();
    List<ServiceDto> getServicesByCategoryId(Long categoryId);
    ServiceDto getServiceById(Long id);
    ServiceDto createService(ServiceDto serviceDto);
    ServiceDto updateService(Long id, ServiceDto serviceDto);
    void deleteService(Long id);
}
