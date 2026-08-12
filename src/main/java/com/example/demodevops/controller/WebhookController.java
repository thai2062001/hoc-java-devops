package com.example.demodevops.controller;

import com.example.demodevops.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/social")
public class WebhookController {

    private final WebhookService webhookService;

    @Autowired
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    // Xác minh cổng Webhook từ Facebook Graph API
    @GetMapping("/facebook")
    public ResponseEntity<String> verifyFacebookWebhook(
            @RequestParam(value = "hub.mode", required = false) String mode,
            @RequestParam(value = "hub.challenge", required = false) String challenge,
            @RequestParam(value = "hub.verify_token", required = false) String verifyToken) {
        
        // Sườn kiểm thử verify token, sau này có thể cấu hình thông qua application.properties
        String localVerifyToken = "my-secret-fb-verify-token"; 

        if ("subscribe".equals(mode) && challenge != null) {
            if (verifyToken == null || verifyToken.equals(localVerifyToken)) {
                return ResponseEntity.ok(challenge);
            }
        }
        return ResponseEntity.badRequest().body("Verification failed");
    }

    // Nhận dữ liệu webhook tin nhắn từ Facebook Messenger
    @PostMapping("/facebook")
    public ResponseEntity<Void> receiveFacebookWebhook(@RequestBody Map<String, Object> payload) {
        webhookService.processFacebookWebhook(payload);
        return ResponseEntity.ok().build();
    }

    // Nhận dữ liệu webhook tin nhắn từ Zalo OA
    @PostMapping("/zalo")
    public ResponseEntity<Void> receiveZaloWebhook(@RequestBody Map<String, Object> payload) {
        webhookService.processZaloWebhook(payload);
        return ResponseEntity.ok().build();
    }

    // Nhận dữ liệu webhook tin nhắn từ TikTok Shop
    @PostMapping("/tiktok")
    public ResponseEntity<Void> receiveTikTokWebhook(@RequestBody Map<String, Object> payload) {
        webhookService.processTikTokWebhook(payload);
        return ResponseEntity.ok().build();
    }

    // Nhận dữ liệu webhook tin nhắn gửi từ MXH nói chung (hàm cũ dùng làm fallback)
    @PostMapping
    public ResponseEntity<Void> receiveMessageWebhook(@RequestBody Map<String, Object> payload) {
        webhookService.processSocialWebhook(payload);
        return ResponseEntity.ok().build();
    }
}
