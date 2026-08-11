package com.example.demodevops.service;

import com.example.demodevops.dto.ServiceCategoryDto;
import java.util.List;

public interface ServiceCategoryService {
    List<ServiceCategoryDto> getAllCategories();
    ServiceCategoryDto getCategoryById(Long id);
    ServiceCategoryDto createCategory(ServiceCategoryDto categoryDto);
    ServiceCategoryDto updateCategory(Long id, ServiceCategoryDto categoryDto);
    void deleteCategory(Long id);
}
