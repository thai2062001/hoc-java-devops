package com.example.demodevops.repository;

import com.example.demodevops.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findBySocialAccountIdAndExternalConversationId(Long socialAccountId, String externalConversationId);
    List<Conversation> findByAssignedEmployeeId(Long employeeId);
}
