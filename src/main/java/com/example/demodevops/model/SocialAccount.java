package com.example.demodevops.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "social_accounts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"platform", "external_page_id"})
)
public class SocialAccount {

    public enum SocialPlatform {
        FACEBOOK, INSTAGRAM, TIKTOK
    }

    public enum ConnectionStatus {
        CONNECTED, DISCONNECTED, EXPIRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SocialPlatform platform;

    @Column(name = "account_name", nullable = false, length = 150)
    private String accountName;

    @Column(name = "external_page_id", nullable = false, length = 150)
    private String externalPageId;

    @Column(name = "access_token", length = 1000)
    private String accessToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connected_by")
    private Employee connectedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConnectionStatus status = ConnectionStatus.CONNECTED;

    @Column(name = "connected_at")
    private LocalDateTime connectedAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public SocialAccount() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SocialPlatform getPlatform() { return platform; }
    public void setPlatform(SocialPlatform platform) { this.platform = platform; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getExternalPageId() { return externalPageId; }
    public void setExternalPageId(String externalPageId) { this.externalPageId = externalPageId; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public Employee getConnectedBy() { return connectedBy; }
    public void setConnectedBy(Employee connectedBy) { this.connectedBy = connectedBy; }

    public ConnectionStatus getStatus() { return status; }
    public void setStatus(ConnectionStatus status) { this.status = status; }

    public LocalDateTime getConnectedAt() { return connectedAt; }
    public void setConnectedAt(LocalDateTime connectedAt) { this.connectedAt = connectedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
