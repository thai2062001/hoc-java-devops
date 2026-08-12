package com.example.demodevops.service;

import java.util.Map;

public interface WebhookService {
    void processSocialWebhook(Map<String, Object> payload);
    void processFacebookWebhook(Map<String, Object> payload);
    void processZaloWebhook(Map<String, Object> payload);
    void processTikTokWebhook(Map<String, Object> payload);
}
