package com.example.demodevops.dto;

import com.example.demodevops.model.Customer.CustomerStatus;
import com.example.demodevops.model.Customer.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerDto {
    private Long id;
    private Long membershipTierId;
    private String membershipTierName;
    private String customerCode;
    private String fullName;
    private String phone;
    private String email;
    private LocalDate dob;
    private Gender gender;
    private String address;
    private String avatarUrl;
    private Integer loyaltyPoints;
    private String skinType;
    private String medicalNotes;
    private String source;
    private CustomerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomerDto() {}

    public CustomerDto(Long id, Long membershipTierId, String membershipTierName, String customerCode, String fullName,
                       String phone, String email, LocalDate dob, Gender gender, String address, String avatarUrl,
                       Integer loyaltyPoints, String skinType, String medicalNotes, String source,
                       CustomerStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.membershipTierId = membershipTierId;
        this.membershipTierName = membershipTierName;
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.avatarUrl = avatarUrl;
        this.loyaltyPoints = loyaltyPoints;
        this.skinType = skinType;
        this.medicalNotes = medicalNotes;
        this.source = source;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMembershipTierId() { return membershipTierId; }
    public void setMembershipTierId(Long membershipTierId) { this.membershipTierId = membershipTierId; }

    public String getMembershipTierName() { return membershipTierName; }
    public void setMembershipTierName(String membershipTierName) { this.membershipTierName = membershipTierName; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public String getSkinType() { return skinType; }
    public void setSkinType(String skinType) { this.skinType = skinType; }

    public String getMedicalNotes() { return medicalNotes; }
    public void setMedicalNotes(String medicalNotes) { this.medicalNotes = medicalNotes; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public CustomerStatus getStatus() { return status; }
    public void setStatus(CustomerStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
