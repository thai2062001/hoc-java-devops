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
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(value = "hub.mode", required = false) String mode,
            @RequestParam(value = "hub.challenge", required = false) String challenge,
            @RequestParam(value = "hub.verify_token", required = false) String verifyToken) {
        
        // Trả về hub.challenge trực tiếp để xác thực kết nối Facebook Developer
        if ("subscribe".equals(mode) && challenge != null) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.badRequest().body("Verification failed");
    }

    // Nhận dữ liệu webhook tin nhắn gửi từ MXH
    @PostMapping
    public ResponseEntity<Void> receiveMessageWebhook(@RequestBody Map<String, Object> payload) {
        webhookService.processSocialWebhook(payload);
        return ResponseEntity.ok().build();
    }
}
