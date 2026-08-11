package com.example.demodevops.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MembershipTierDto {
    private Long id;
    private String code;
    private String name;
    private Integer minPoints;
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
