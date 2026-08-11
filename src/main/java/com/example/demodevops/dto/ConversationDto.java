package com.example.demodevops.dto;

import com.example.demodevops.model.Conversation.ConversationStatus;

import java.time.LocalDateTime;

public class ConversationDto {
    private Long id;
    private String platform;
    private String accountName;
    private Long customerId;
    private String customerName;
    private String externalConversationId;
    private String customerSocialName;
    private String customerAvatarUrl;
    private Long assignedEmployeeId;
    private String assignedEmployeeName;
    private ConversationStatus status;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ConversationDto() {}

    public ConversationDto(Long id, String platform, String accountName, Long customerId, String customerName,
                           String externalConversationId, String customerSocialName, String customerAvatarUrl,
                           Long assignedEmployeeId, String assignedEmployeeName, ConversationStatus status,
                           LocalDateTime lastMessageAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.platform = platform;
        this.accountName = accountName;
        this.customerId = customerId;
        this.customerName = customerName;
        this.externalConversationId = externalConversationId;
        this.customerSocialName = customerSocialName;
        this.customerAvatarUrl = customerAvatarUrl;
        this.assignedEmployeeId = assignedEmployeeId;
        this.assignedEmployeeName = assignedEmployeeName;
        this.status = status;
        this.lastMessageAt = lastMessageAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getExternalConversationId() { return externalConversationId; }
    public void setExternalConversationId(String externalConversationId) { this.externalConversationId = externalConversationId; }

    public String getCustomerSocialName() { return customerSocialName; }
    public void setCustomerSocialName(String customerSocialName) { this.customerSocialName = customerSocialName; }

    public String getCustomerAvatarUrl() { return customerAvatarUrl; }
    public void setCustomerAvatarUrl(String customerAvatarUrl) { this.customerAvatarUrl = customerAvatarUrl; }

    public Long getAssignedEmployeeId() { return assignedEmployeeId; }
    public void setAssignedEmployeeId(Long assignedEmployeeId) { this.assignedEmployeeId = assignedEmployeeId; }

    public String getAssignedEmployeeName() { return assignedEmployeeName; }
    public void setAssignedEmployeeName(String assignedEmployeeName) { this.assignedEmployeeName = assignedEmployeeName; }

    public ConversationStatus getStatus() { return status; }
    public void setStatus(ConversationStatus status) { this.status = status; }

    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
