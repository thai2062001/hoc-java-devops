package com.example.demodevops.service;

import com.example.demodevops.dto.BannerDto;
import com.example.demodevops.dto.BlogPostDto;
import com.example.demodevops.dto.PromotionDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.Banner;
import com.example.demodevops.model.BlogPost;
import com.example.demodevops.model.BlogPost.PostStatus;
import com.example.demodevops.model.Employee;
import com.example.demodevops.model.Promotion;
import com.example.demodevops.model.Promotion.DiscountType;
import com.example.demodevops.repository.BannerRepository;
import com.example.demodevops.repository.BlogPostRepository;
import com.example.demodevops.repository.EmployeeRepository;
import com.example.demodevops.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CmsServiceImpl implements CmsService {

    private final BlogPostRepository blogPostRepository;
    private final BannerRepository bannerRepository;
    private final PromotionRepository promotionRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public CmsServiceImpl(BlogPostRepository blogPostRepository,
                          BannerRepository bannerRepository,
                          PromotionRepository promotionRepository,
                          EmployeeRepository employeeRepository) {
        this.blogPostRepository = blogPostRepository;
        this.bannerRepository = bannerRepository;
        this.promotionRepository = promotionRepository;
        this.employeeRepository = employeeRepository;
    }

    // --- BLOG POSTS ---
    @Override
    public List<BlogPostDto> getPublishedBlogPosts() {
        return blogPostRepository.findByStatusOrderByPublishedAtDesc(PostStatus.PUBLISHED).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BlogPostDto> getAllBlogPosts() {
        return blogPostRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BlogPostDto getBlogPostById(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));
        return convertToDto(post);
    }

    @Override
    public BlogPostDto createBlogPost(BlogPostDto dto, Long authorId) {
        Employee author = authorId != null ? employeeRepository.findById(authorId).orElse(null) : null;
        
        BlogPost post = new BlogPost();
        post.setTitle(dto.getTitle());
        post.setSlug(dto.getSlug());
        post.setExcerpt(dto.getSummary()); // summary DTO maps to excerpt
        post.setContent(dto.getContent());
        post.setThumbnailUrl(dto.getFeaturedImageUrl()); // featuredImageUrl DTO maps to thumbnailUrl
        post.setAuthor(author);
        
        boolean published = dto.getPublished() != null ? dto.getPublished() : false;
        post.setStatus(published ? PostStatus.PUBLISHED : PostStatus.DRAFT);
        if (published) {
            post.setPublishedAt(LocalDateTime.now());
        }

        BlogPost saved = blogPostRepository.save(post);
        return convertToDto(saved);
    }

    @Override
    public BlogPostDto updateBlogPost(Long id, BlogPostDto dto) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));

        post.setTitle(dto.getTitle());
        post.setSlug(dto.getSlug());
        post.setExcerpt(dto.getSummary()); // summary DTO maps to excerpt
        post.setContent(dto.getContent());
        post.setThumbnailUrl(dto.getFeaturedImageUrl()); // featuredImageUrl DTO maps to thumbnailUrl
        
        boolean wasPublished = post.getStatus() == PostStatus.PUBLISHED;
        boolean nowPublished = dto.getPublished() != null ? dto.getPublished() : false;
        
        if (nowPublished != wasPublished) {
            post.setStatus(nowPublished ? PostStatus.PUBLISHED : PostStatus.DRAFT);
            post.setPublishedAt(nowPublished ? LocalDateTime.now() : null);
        }

        BlogPost updated = blogPostRepository.save(post);
        return convertToDto(updated);
    }

    @Override
    public void deleteBlogPost(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));
        blogPostRepository.delete(post);
    }

    // --- BANNERS ---
    @Override
    public List<BannerDto> getActiveBanners() {
        return bannerRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BannerDto> getAllBanners() {
        return bannerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BannerDto createBanner(BannerDto dto) {
        Banner banner = new Banner();
        banner.setTitle(dto.getTitle());
        banner.setImageUrl(dto.getImageUrl());
        banner.setLinkUrl(dto.getTargetUrl()); // targetUrl DTO maps to linkUrl
        banner.setDisplayOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0); // sortOrder DTO maps to displayOrder
        banner.setActive(dto.getActive() != null ? dto.getActive() : true);

        Banner saved = bannerRepository.save(banner);
        return convertToDto(saved);
    }

    @Override
    public BannerDto updateBanner(Long id, BannerDto dto) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found with id: " + id));

        banner.setTitle(dto.getTitle());
        banner.setImageUrl(dto.getImageUrl());
        banner.setLinkUrl(dto.getTargetUrl()); // targetUrl DTO maps to linkUrl
        banner.setDisplayOrder(dto.getSortOrder() != null ? dto.getSortOrder() : banner.getDisplayOrder()); // sortOrder DTO maps to displayOrder
        banner.setActive(dto.getActive() != null ? dto.getActive() : banner.getActive());

        Banner updated = bannerRepository.save(banner);
        return convertToDto(updated);
    }

    @Override
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found with id: " + id));
        bannerRepository.delete(banner);
    }

    // --- PROMOTIONS ---
    @Override
    public List<PromotionDto> getActivePromotions() {
        return promotionRepository.findActivePromotions(LocalDateTime.now()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionDto> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionDto getPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findBySlug(code) // code DTO maps to slug
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with code: " + code));
        return convertToDto(promotion);
    }

    @Override
    public PromotionDto createPromotion(PromotionDto dto) {
        Promotion promotion = new Promotion();
        promotion.setTitle(dto.getName()); // name DTO maps to title
        promotion.setDescription(dto.getDescription());
        promotion.setSlug(dto.getCode()); // code DTO maps to slug
        promotion.setDiscountValue(dto.getDiscountValue());
        promotion.setDiscountType(DiscountType.valueOf(dto.getDiscountType().toUpperCase()));
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setActive(dto.getActive() != null ? dto.getActive() : true);

        Promotion saved = promotionRepository.save(promotion);
        return convertToDto(saved);
    }

    @Override
    public PromotionDto updatePromotion(Long id, PromotionDto dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));

        promotion.setTitle(dto.getName()); // name DTO maps to title
        promotion.setDescription(dto.getDescription());
        promotion.setSlug(dto.getCode()); // code DTO maps to slug
        promotion.setDiscountValue(dto.getDiscountValue());
        promotion.setDiscountType(DiscountType.valueOf(dto.getDiscountType().toUpperCase()));
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setActive(dto.getActive() != null ? dto.getActive() : promotion.getActive());

        Promotion updated = promotionRepository.save(promotion);
        return convertToDto(updated);
    }

    @Override
    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
        promotionRepository.delete(promotion);
    }

    // --- MAPPING HELPERS ---
    private BlogPostDto convertToDto(BlogPost post) {
        return new BlogPostDto(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getExcerpt(), // excerpt maps to summary
                post.getContent(),
                post.getThumbnailUrl(), // thumbnailUrl maps to featuredImageUrl
                post.getAuthor() != null ? post.getAuthor().getId() : null,
                post.getAuthor() != null ? post.getAuthor().getFullName() : "Anonymous",
                post.getStatus() == PostStatus.PUBLISHED,
                post.getPublishedAt(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    private BannerDto convertToDto(Banner banner) {
        return new BannerDto(
                banner.getId(),
                banner.getTitle(),
                "", // Banner has no subtitle in DB, return empty
                banner.getImageUrl(),
                banner.getLinkUrl(), // linkUrl maps to targetUrl
                banner.getDisplayOrder(), // displayOrder maps to sortOrder
                banner.getActive(),
                banner.getCreatedAt(),
                banner.getUpdatedAt()
        );
    }

    private PromotionDto convertToDto(Promotion p) {
        return new PromotionDto(
                p.getId(),
                p.getTitle(), // title maps to name
                p.getDescription(),
                p.getSlug(), // slug maps to code
                p.getDiscountValue(),
                p.getDiscountType().name(),
                java.math.BigDecimal.ZERO, // not in DB
                java.math.BigDecimal.ZERO, // not in DB
                p.getStartDate(),
                p.getEndDate(),
                0, // not in DB
                0, // not in DB
                p.getActive(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
