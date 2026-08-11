package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.BannerDto;
import com.example.demodevops.dto.BlogPostDto;
import com.example.demodevops.dto.PromotionDto;
import com.example.demodevops.security.EmployeePrincipal;
import com.example.demodevops.service.CmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cms")
public class CmsController {

    private final CmsService cmsService;

    @Autowired
    public CmsController(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    // --- PUBLIC CMS READ ENDPOINTS ---
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<BlogPostDto>>> getPublishedPosts() {
        List<BlogPostDto> posts = cmsService.getPublishedBlogPosts();
        return ResponseEntity.ok(ApiResponse.success(posts, "Retrieved published blog posts successfully"));
    }

    @GetMapping("/banners")
    public ResponseEntity<ApiResponse<List<BannerDto>>> getActiveBanners() {
        List<BannerDto> banners = cmsService.getActiveBanners();
        return ResponseEntity.ok(ApiResponse.success(banners, "Retrieved active banners successfully"));
    }

    @GetMapping("/promotions")
    public ResponseEntity<ApiResponse<List<PromotionDto>>> getActivePromotions() {
        List<PromotionDto> promotions = cmsService.getActivePromotions();
        return ResponseEntity.ok(ApiResponse.success(promotions, "Retrieved active promotions successfully"));
    }

    // --- SECURED CMS MANAGEMENT ENDPOINTS ---
    @GetMapping("/admin/posts")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<BlogPostDto>>> getAllPosts() {
        List<BlogPostDto> posts = cmsService.getAllBlogPosts();
        return ResponseEntity.ok(ApiResponse.success(posts, "Retrieved all blog posts successfully"));
    }

    @PostMapping("/admin/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BlogPostDto>> createPost(
            @RequestBody BlogPostDto dto,
            @AuthenticationPrincipal EmployeePrincipal principal) {
        Long authorId = principal != null ? principal.getEmployee().getId() : null;
        BlogPostDto created = cmsService.createBlogPost(dto, authorId);
        return new ResponseEntity<>(ApiResponse.success(created, "Blog post created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/admin/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BlogPostDto>> updatePost(@PathVariable Long id, @RequestBody BlogPostDto dto) {
        BlogPostDto updated = cmsService.updateBlogPost(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Blog post updated successfully"));
    }

    @DeleteMapping("/admin/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        cmsService.deleteBlogPost(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Blog post deleted successfully"));
    }
}
