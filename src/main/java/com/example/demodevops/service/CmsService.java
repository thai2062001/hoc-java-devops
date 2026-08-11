package com.example.demodevops.service;

import com.example.demodevops.dto.BannerDto;
import com.example.demodevops.dto.BlogPostDto;
import com.example.demodevops.dto.PromotionDto;

import java.util.List;

public interface CmsService {
    // Blog Posts
    List<BlogPostDto> getPublishedBlogPosts();
    List<BlogPostDto> getAllBlogPosts();
    BlogPostDto getBlogPostById(Long id);
    BlogPostDto createBlogPost(BlogPostDto blogPostDto, Long authorId);
    BlogPostDto updateBlogPost(Long id, BlogPostDto blogPostDto);
    void deleteBlogPost(Long id);

    // Banners
    List<BannerDto> getActiveBanners();
    List<BannerDto> getAllBanners();
    BannerDto createBanner(BannerDto bannerDto);
    BannerDto updateBanner(Long id, BannerDto bannerDto);
    void deleteBanner(Long id);

    // Promotions
    List<PromotionDto> getActivePromotions();
    List<PromotionDto> getAllPromotions();
    PromotionDto getPromotionByCode(String code);
    PromotionDto createPromotion(PromotionDto promotionDto);
    PromotionDto updatePromotion(Long id, PromotionDto promotionDto);
    void deletePromotion(Long id);
}
