package com.example.demodevops.service;

import com.example.demodevops.dto.ConversationDto;
import com.example.demodevops.dto.MessageDto;

import java.util.List;

public interface ChatService {
    List<ConversationDto> getAllConversations();
    List<ConversationDto> getConversationsByStaff(Long staffId);
    ConversationDto getConversationById(Long id);
    List<MessageDto> getMessageHistory(Long conversationId);
    MessageDto sendReply(Long conversationId, String content, Long employeeId);
    ConversationDto assignStaff(Long conversationId, Long staffId);
}
