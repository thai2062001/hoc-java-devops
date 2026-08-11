package com.example.demodevops.dto;

import com.example.demodevops.model.Customer.CustomerStatus;
import com.example.demodevops.model.Customer.Gender;

import java.time.LocalDate;

public class CustomerSaveDto {
    private String customerCode;
    private String fullName;
    private String phone;
    private String email;
    private LocalDate dob;
    private Gender gender;
    private String address;
    private String avatarUrl;
    private String skinType;
    private String medicalNotes;
    private String source;
    private CustomerStatus status;

    public CustomerSaveDto() {}

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

    public String getSkinType() { return skinType; }
    public void setSkinType(String skinType) { this.skinType = skinType; }

    public String getMedicalNotes() { return medicalNotes; }
    public void setMedicalNotes(String medicalNotes) { this.medicalNotes = medicalNotes; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public CustomerStatus getStatus() { return status; }
    public void setStatus(CustomerStatus status) { this.status = status; }
}
