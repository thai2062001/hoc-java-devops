package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.ServiceCategoryDto;
import com.example.demodevops.service.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ServiceCategoryController {

    private final ServiceCategoryService categoryService;

    @Autowired
    public ServiceCategoryController(ServiceCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceCategoryDto>>> getAllCategories() {
        List<ServiceCategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "Retrieved all categories successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceCategoryDto>> getCategoryById(@PathVariable Long id) {
        ServiceCategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category, "Retrieved category successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceCategoryDto>> createCategory(@Valid @RequestBody ServiceCategoryDto categoryDto) {
        ServiceCategoryDto created = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(ApiResponse.success(created, "Created category successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceCategoryDto>> updateCategory(@PathVariable Long id, @Valid @RequestBody ServiceCategoryDto categoryDto) {
        ServiceCategoryDto updated = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Updated category successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted category successfully"));
    }
}
