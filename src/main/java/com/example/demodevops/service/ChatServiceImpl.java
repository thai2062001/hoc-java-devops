package com.example.demodevops.service;

import com.example.demodevops.dto.ConversationDto;
import com.example.demodevops.dto.MessageDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.Conversation;
import com.example.demodevops.model.Employee;
import com.example.demodevops.model.Message;
import com.example.demodevops.model.Message.SenderType;
import com.example.demodevops.repository.ConversationRepository;
import com.example.demodevops.repository.EmployeeRepository;
import com.example.demodevops.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public ChatServiceImpl(ConversationRepository conversationRepository,
                           MessageRepository messageRepository,
                           EmployeeRepository employeeRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<ConversationDto> getAllConversations() {
        return conversationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConversationDto> getConversationsByStaff(Long staffId) {
        return conversationRepository.findByAssignedEmployeeId(staffId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ConversationDto getConversationById(Long id) {
        Conversation conv = conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + id));
        return convertToDto(conv);
    }

    @Override
    public List<MessageDto> getMessageHistory(Long conversationId) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + conversationId));

        // Đánh dấu tất cả tin nhắn trong hội thoại là đã đọc
        List<Message> messages = messageRepository.findByConversationIdOrderBySentAtAsc(conversationId);
        for (Message msg : messages) {
            if (msg.getSenderType() == SenderType.CUSTOMER && !msg.getRead()) {
                msg.setRead(true);
                messageRepository.save(msg);
            }
        }

        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MessageDto sendReply(Long conversationId, String content, Long employeeId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + conversationId));

        Employee staff = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        Message msg = new Message();
        msg.setConversation(conversation);
        msg.setSenderType(SenderType.EMPLOYEE);
        msg.setEmployee(staff);
        msg.setContent(content);
        msg.setRead(true);
        msg.setSentAt(LocalDateTime.now());
        msg.setExternalMessageId("repl-" + System.currentTimeMillis());

        Message saved = messageRepository.save(msg);

        // Cập nhật mốc thời gian tin nhắn mới nhất trong Conversation
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        // Giả lập gửi tin nhắn phản hồi tới Social API thực tế ở đây (Webhook callback)

        return convertToDto(saved);
    }

    @Override
    public ConversationDto assignStaff(Long conversationId, Long staffId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + conversationId));

        Employee staff = employeeRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + staffId));

        conversation.setAssignedEmployee(staff);
        Conversation updated = conversationRepository.save(conversation);
        return convertToDto(updated);
    }

    private ConversationDto convertToDto(Conversation c) {
        return new ConversationDto(
                c.getId(),
                c.getSocialAccount().getPlatform().name(),
                c.getSocialAccount().getAccountName(),
                c.getCustomer() != null ? c.getCustomer().getId() : null,
                c.getCustomer() != null ? c.getCustomer().getFullName() : null,
                c.getExternalConversationId(),
                c.getCustomerSocialName(),
                c.getCustomerAvatarUrl(),
                c.getAssignedEmployee() != null ? c.getAssignedEmployee().getId() : null,
                c.getAssignedEmployee() != null ? c.getAssignedEmployee().getFullName() : null,
                c.getStatus(),
                c.getLastMessageAt(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }

    private MessageDto convertToDto(Message m) {
        return new MessageDto(
                m.getId(),
                m.getConversation().getId(),
                m.getSenderType(),
                m.getEmployee() != null ? m.getEmployee().getId() : null,
                m.getEmployee() != null ? m.getEmployee().getFullName() : null,
                m.getContent(),
                m.getAttachmentUrl(),
                m.getExternalMessageId(),
                m.getRead(),
                m.getSentAt()
        );
    }
}
