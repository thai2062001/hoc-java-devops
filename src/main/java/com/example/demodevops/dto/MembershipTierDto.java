package com.example.demodevops.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MembershipTierDto {
    private Long id;

    @NotBlank(message = "Tier code is required")
    private String code;

    @NotBlank(message = "Tier name is required")
    private String name;

    @NotNull(message = "Minimum points are required")
    @Min(value = 0, message = "Minimum points must be at least 0")
    private Integer minPoints;

    @NotNull(message = "Discount percent is required")
    @Min(value = 0, message = "Discount percent must be at least 0")
    private BigDecimal discountPercent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MembershipTierDto() {}

    public MembershipTierDto(Long id, String code, String name, Integer minPoints, BigDecimal discountPercent,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.minPoints = minPoints;
        this.discountPercent = discountPercent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getMinPoints() { return minPoints; }
    public void setMinPoints(Integer minPoints) { this.minPoints = minPoints; }

    public BigDecimal getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
