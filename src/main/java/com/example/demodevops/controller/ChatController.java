package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.ConversationDto;
import com.example.demodevops.dto.MessageDto;
import com.example.demodevops.security.EmployeePrincipal;
import com.example.demodevops.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<ConversationDto>>> getAllConversations() {
        List<ConversationDto> conversations = chatService.getAllConversations();
        return ResponseEntity.ok(ApiResponse.success(conversations, "Retrieved all chat threads successfully"));
    }

    @GetMapping("/my-chats")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<ConversationDto>>> getMyConversations(
            @AuthenticationPrincipal EmployeePrincipal principal) {
        
        Long staffId = principal != null ? principal.getEmployee().getId() : null;
        List<ConversationDto> conversations = chatService.getConversationsByStaff(staffId);
        return ResponseEntity.ok(ApiResponse.success(conversations, "Retrieved your assigned chat threads successfully"));
    }

    @GetMapping("/conversation/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getMessageHistory(@PathVariable Long id) {
        List<MessageDto> history = chatService.getMessageHistory(id);
        return ResponseEntity.ok(ApiResponse.success(history, "Retrieved message history successfully"));
    }

    @PostMapping("/conversation/{id}/reply")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<MessageDto>> sendReply(
            @PathVariable Long id,
            @RequestBody MessageDto replyDto,
            @AuthenticationPrincipal EmployeePrincipal principal) {
        
        Long employeeId = principal != null ? principal.getEmployee().getId() : null;
        MessageDto reply = chatService.sendReply(id, replyDto.getContent(), employeeId);
        return ResponseEntity.ok(ApiResponse.success(reply, "Reply sent successfully"));
    }

    @PutMapping("/conversation/{id}/assign/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<ConversationDto>> assignConversation(
            @PathVariable Long id,
            @PathVariable Long staffId) {
        ConversationDto conversation = chatService.assignStaff(id, staffId);
        return ResponseEntity.ok(ApiResponse.success(conversation, "Conversation assigned successfully"));
    }
}
