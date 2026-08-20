package com.fitple.fitple.repository;

import com.fitple.fitple.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long roomId);
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(
            Long roomId,
            Pageable pageable
    );
    List<ChatMessage> findByChatRoomProjectIdOrderByCreatedAtAsc(Long projectId);

}