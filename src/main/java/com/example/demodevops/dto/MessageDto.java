package com.example.demodevops.dto;

import com.example.demodevops.model.Message.SenderType;

import java.time.LocalDateTime;

public class MessageDto {
    private Long id;
    private Long conversationId;
    private SenderType senderType;
    private Long employeeId;
    private String employeeName;
    private String content;
    private String attachmentUrl;
    private String externalMessageId;
    private Boolean isRead;
    private LocalDateTime sentAt;

    public MessageDto() {}

    public MessageDto(Long id, Long conversationId, SenderType senderType, Long employeeId, String employeeName,
                      String content, String attachmentUrl, String externalMessageId, Boolean isRead,
                      LocalDateTime sentAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderType = senderType;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.content = content;
        this.attachmentUrl = attachmentUrl;
        this.externalMessageId = externalMessageId;
        this.isRead = isRead;
        this.sentAt = sentAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public SenderType getSenderType() { return senderType; }
    public void setSenderType(SenderType senderType) { this.senderType = senderType; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getExternalMessageId() { return externalMessageId; }
    public void setExternalMessageId(String externalMessageId) { this.externalMessageId = externalMessageId; }

    public Boolean getRead() { return isRead; }
    public void setRead(Boolean read) { isRead = read; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
