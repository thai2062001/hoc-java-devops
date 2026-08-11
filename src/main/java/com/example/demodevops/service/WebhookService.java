package com.example.demodevops.service;

import java.util.Map;

public interface WebhookService {
    void processSocialWebhook(Map<String, Object> payload);
}
