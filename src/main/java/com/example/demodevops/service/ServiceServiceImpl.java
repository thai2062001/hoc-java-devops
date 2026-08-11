package com.example.demodevops.service;

import com.example.demodevops.dto.ServiceDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.Service;
import com.example.demodevops.model.ServiceCategory;
import com.example.demodevops.repository.ServiceCategoryRepository;
import com.example.demodevops.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceCategoryRepository categoryRepository;

    @Autowired
    public ServiceServiceImpl(ServiceRepository serviceRepository, ServiceCategoryRepository categoryRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<ServiceDto> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDto> getServicesByCategoryId(Long categoryId) {
        return serviceRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceDto getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return convertToDto(service);
    }

    @Override
    public ServiceDto createService(ServiceDto serviceDto) {
        ServiceCategory category = categoryRepository.findById(serviceDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Service Category not found with id: " + serviceDto.getCategoryId()));

        Service service = new Service();
        service.setCategory(category);
        service.setName(serviceDto.getName());
        service.setDescription(serviceDto.getDescription());
        service.setPrice(serviceDto.getPrice());
        service.setDurationMinutes(serviceDto.getDurationMinutes());
        service.setImageUrl(serviceDto.getImageUrl());
        service.setActive(serviceDto.getActive() != null ? serviceDto.getActive() : true);

        Service savedService = serviceRepository.save(service);
        return convertToDto(savedService);
    }

    @Override
    public ServiceDto updateService(Long id, ServiceDto serviceDto) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        ServiceCategory category = categoryRepository.findById(serviceDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Service Category not found with id: " + serviceDto.getCategoryId()));

        service.setCategory(category);
        service.setName(serviceDto.getName());
        service.setDescription(serviceDto.getDescription());
        service.setPrice(serviceDto.getPrice());
        service.setDurationMinutes(serviceDto.getDurationMinutes());
        service.setImageUrl(serviceDto.getImageUrl());
        if (serviceDto.getActive() != null) {
            service.setActive(serviceDto.getActive());
        }

        Service updatedService = serviceRepository.save(service);
        return convertToDto(updatedService);
    }

    @Override
    public void deleteService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        serviceRepository.delete(service);
    }

    private ServiceDto convertToDto(Service service) {
        return new ServiceDto(
                service.getId(),
                service.getCategory().getId(),
                service.getCategory().getName(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getDurationMinutes(),
                service.getImageUrl(),
                service.getActive(),
                service.getCreatedAt(),
                service.getUpdatedAt()
        );
    }
}
