package com.fitple.fitple.repository;

import com.fitple.fitple.domain.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatFileRepository extends JpaRepository<ChatFile, Long> {
    List<ChatFile> findByChatMessageChatRoomIdOrderByIdDesc(Long roomId);
}