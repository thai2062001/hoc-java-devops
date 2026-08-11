package com.example.demodevops.service;

import com.example.demodevops.model.Conversation;
import com.example.demodevops.model.Conversation.ConversationStatus;
import com.example.demodevops.model.Message;
import com.example.demodevops.model.Message.SenderType;
import com.example.demodevops.model.SocialAccount;
import com.example.demodevops.model.SocialAccount.SocialPlatform;
import com.example.demodevops.repository.ConversationRepository;
import com.example.demodevops.repository.MessageRepository;
import com.example.demodevops.repository.SocialAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional
public class WebhookServiceImpl implements WebhookService {

    private final SocialAccountRepository socialAccountRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public WebhookServiceImpl(SocialAccountRepository socialAccountRepository,
                              ConversationRepository conversationRepository,
                              MessageRepository messageRepository) {
        this.socialAccountRepository = socialAccountRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void processSocialWebhook(Map<String, Object> payload) {
        // Trích xuất các trường từ payload giả định cấu trúc Facebook Messenger Webhook
        // Ví dụ: { "platform": "FACEBOOK", "pageId": "123456", "senderId": "78910", "senderName": "Nguyen Van A", "messageId": "mid.123", "text": "Hello Spa", "timestamp": 1690000000000 }
        
        String platformStr = (String) payload.getOrDefault("platform", "FACEBOOK");
        String pageId = (String) payload.getOrDefault("pageId", "default-page-id");
        String senderId = (String) payload.getOrDefault("senderId", "default-sender-id");
        String senderName = (String) payload.getOrDefault("senderName", "Guest Social User");
        String messageId = (String) payload.getOrDefault("messageId", "msg-" + System.currentTimeMillis());
        String text = (String) payload.getOrDefault("text", "");
        String attachmentUrl = (String) payload.get("attachmentUrl");

        SocialPlatform platform = SocialPlatform.valueOf(platformStr.toUpperCase());

        // Tìm hoặc tự động khởi tạo SocialAccount để tránh lỗi khoá ngoại nếu chưa cấu hình
        SocialAccount socialAccount = socialAccountRepository
                .findByPlatformAndExternalPageId(platform, pageId)
                .orElseGet(() -> {
                    SocialAccount acc = new SocialAccount();
                    acc.setPlatform(platform);
                    acc.setExternalPageId(pageId);
                    acc.setAccountName("Auto Configured " + platformStr);
                    acc.setStatus(SocialAccount.ConnectionStatus.CONNECTED);
                    acc.setConnectedAt(LocalDateTime.now());
                    return socialAccountRepository.save(acc);
                });

        // Tìm hoặc tạo mới Conversation
        Conversation conversation = conversationRepository
                .findBySocialAccountIdAndExternalConversationId(socialAccount.getId(), senderId)
                .orElseGet(() -> {
                    Conversation conv = new Conversation();
                    conv.setSocialAccount(socialAccount);
                    conv.setExternalConversationId(senderId);
                    conv.setCustomerSocialName(senderName);
                    conv.setStatus(ConversationStatus.OPEN);
                    return conversationRepository.save(conv);
                });

        // Tạo Message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderType(SenderType.CUSTOMER);
        message.setContent(text);
        message.setAttachmentUrl(attachmentUrl);
        message.setExternalMessageId(messageId);
        message.setRead(false);
        message.setSentAt(LocalDateTime.now());

        messageRepository.save(message);

        // Cập nhật mốc thời gian tin nhắn mới nhất trong Conversation
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }
}
