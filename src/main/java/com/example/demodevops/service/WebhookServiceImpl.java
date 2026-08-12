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

    @Override
    public void processFacebookWebhook(Map<String, Object> payload) {
        // =====================================================================
        // SƯỜN KẾT NỐI FACEBOOK MESSENGER WEBHOOK
        // =====================================================================
        // Payload thực tế của FB gửi về thường có cấu trúc:
        // {
        //   "object": "page",
        //   "entry": [
        //     {
        //       "id": "PAGE_ID",
        //       "messaging": [
        //         {
        //           "sender": { "id": "USER_ID" },
        //           "recipient": { "id": "PAGE_ID" },
        //           "message": { "mid": "mid.123", "text": "Tin nhắn từ FB", "attachments": [...] }
        //         }
        //       ]
        //     }
        //   ]
        // }
        try {
            if ("page".equals(payload.get("object")) && payload.containsKey("entry")) {
                java.util.List<Map<String, Object>> entries = (java.util.List<Map<String, Object>>) payload.get("entry");
                for (Map<String, Object> entry : entries) {
                    String pageId = (String) entry.get("id");
                    if (entry.containsKey("messaging")) {
                        java.util.List<Map<String, Object>> messagings = (java.util.List<Map<String, Object>>) entry.get("messaging");
                        for (Map<String, Object> messaging : messagings) {
                            Map<String, Object> sender = (Map<String, Object>) messaging.get("sender");
                            String senderId = sender != null ? (String) sender.get("id") : "unknown";

                            Map<String, Object> messageMap = (Map<String, Object>) messaging.get("message");
                            if (messageMap != null) {
                                String text = (String) messageMap.get("text");
                                String messageId = (String) messageMap.get("mid");
                                String attachmentUrl = null;

                                // Trích xuất ảnh/file đính kèm nếu có
                                if (messageMap.containsKey("attachments")) {
                                    java.util.List<Map<String, Object>> attachments = (java.util.List<Map<String, Object>>) messageMap.get("attachments");
                                    if (!attachments.isEmpty()) {
                                        Map<String, Object> payloadAttachment = (Map<String, Object>) attachments.get(0).get("payload");
                                        if (payloadAttachment != null) {
                                            attachmentUrl = (String) payloadAttachment.get("url");
                                        }
                                    }
                                }

                                // Bản ghi mapping lưu trữ thông tin
                                Map<String, Object> parsedPayload = new java.util.HashMap<>();
                                parsedPayload.put("platform", "FACEBOOK");
                                parsedPayload.put("pageId", pageId);
                                parsedPayload.put("senderId", senderId);
                                parsedPayload.put("senderName", "Facebook User " + senderId);
                                parsedPayload.put("messageId", messageId);
                                parsedPayload.put("text", text);
                                parsedPayload.put("attachmentUrl", attachmentUrl);

                                // Gọi xử lý nghiệp vụ chung để lưu DB
                                processSocialWebhook(parsedPayload);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Log lỗi parsing ở đây
            System.err.println("Error parsing Facebook webhook: " + e.getMessage());
        }
    }

    @Override
    public void processZaloWebhook(Map<String, Object> payload) {
        // =====================================================================
        // SƯỜN KẾT NỐI ZALO OA WEBHOOK
        // =====================================================================
        // Payload thực tế của Zalo OA gửi về thường có cấu trúc:
        // {
        //   "oa_id": "OA_ID",
        //   "event_name": "user_send_text",
        //   "sender": { "id": "USER_ID" },
        //   "message": { "text": "Tin nhắn từ Zalo", "msg_id": "zalo-msg-123" },
        //   "timestamp": "169000000"
        // }
        try {
            String eventName = (String) payload.get("event_name");
            String oaId = (String) payload.get("oa_id");
            Map<String, Object> sender = (Map<String, Object>) payload.get("sender");
            String senderId = sender != null ? (String) sender.get("id") : null;

            if (senderId != null && ("user_send_text".equals(eventName) || "user_send_image".equals(eventName))) {
                Map<String, Object> messageMap = (Map<String, Object>) payload.get("message");
                if (messageMap != null) {
                    String text = (String) messageMap.get("text");
                    String messageId = (String) messageMap.get("msg_id");
                    String attachmentUrl = null;

                    // Nếu gửi ảnh, trích xuất URL ảnh
                    if (messageMap.containsKey("attachments")) {
                        java.util.List<Map<String, Object>> attachments = (java.util.List<Map<String, Object>>) messageMap.get("attachments");
                        if (!attachments.isEmpty()) {
                            Map<String, Object> payloadAttachment = (Map<String, Object>) attachments.get(0).get("payload");
                            if (payloadAttachment != null) {
                                attachmentUrl = (String) payloadAttachment.get("url");
                            }
                        }
                    }

                    Map<String, Object> parsedPayload = new java.util.HashMap<>();
                    parsedPayload.put("platform", "ZALO");
                    parsedPayload.put("pageId", oaId);
                    parsedPayload.put("senderId", senderId);
                    parsedPayload.put("senderName", "Zalo User " + senderId);
                    parsedPayload.put("messageId", messageId);
                    parsedPayload.put("text", text != null ? text : "[Media/Attachment]");
                    parsedPayload.put("attachmentUrl", attachmentUrl);

                    processSocialWebhook(parsedPayload);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing Zalo webhook: " + e.getMessage());
        }
    }

    @Override
    public void processTikTokWebhook(Map<String, Object> payload) {
        // =====================================================================
        // SƯỜN KẾT NỐI TIKTOK SHOP/CHAT WEBHOOK
        // =====================================================================
        // Payload thực tế của TikTok Shop Webhook gửi về thường có cấu trúc:
        // {
        //   "event": "conversation.message.new",
        //   "shop_id": "SHOP_ID",
        //   "content": "{\"sender\":{\"id\":\"SENDER_ID\"},\"message_id\":\"msg-456\",\"text\":\"Tin nhắn TikTok\"}"
        // }
        try {
            String event = (String) payload.get("event");
            String shopId = (String) payload.get("shop_id");
            
            // TikTok thường gửi tin nhắn mới với event "conversation.message.new" hoặc tương đương
            if (event != null && event.contains("message")) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data"); // Một số version để trong block 'data'
                if (data == null) {
                    data = payload;
                }
                
                String senderId = (String) data.get("sender_id");
                String text = (String) data.get("text");
                String messageId = (String) data.get("message_id");

                if (senderId != null) {
                    Map<String, Object> parsedPayload = new java.util.HashMap<>();
                    parsedPayload.put("platform", "TIKTOK");
                    parsedPayload.put("pageId", shopId != null ? shopId : "default-tiktok-shop");
                    parsedPayload.put("senderId", senderId);
                    parsedPayload.put("senderName", "TikTok User " + senderId);
                    parsedPayload.put("messageId", messageId != null ? messageId : "tt-msg-" + System.currentTimeMillis());
                    parsedPayload.put("text", text);
                    parsedPayload.put("attachmentUrl", null);

                    processSocialWebhook(parsedPayload);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing TikTok webhook: " + e.getMessage());
        }
    }
}
