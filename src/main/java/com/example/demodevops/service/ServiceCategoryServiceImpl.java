package com.example.demodevops.service;

import com.example.demodevops.dto.ServiceCategoryDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.ServiceCategory;
import com.example.demodevops.repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private final ServiceCategoryRepository categoryRepository;

    @Autowired
    public ServiceCategoryServiceImpl(ServiceCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<ServiceCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceCategoryDto getCategoryById(Long id) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service Category not found with id: " + id));
        return convertToDto(category);
    }

    @Override
    public ServiceCategoryDto createCategory(ServiceCategoryDto categoryDto) {
        ServiceCategory category = new ServiceCategory();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        ServiceCategory savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    @Override
    public ServiceCategoryDto updateCategory(Long id, ServiceCategoryDto categoryDto) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service Category not found with id: " + id));
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        ServiceCategory updatedCategory = categoryRepository.save(category);
        return convertToDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    private ServiceCategoryDto convertToDto(ServiceCategory category) {
        return new ServiceCategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
