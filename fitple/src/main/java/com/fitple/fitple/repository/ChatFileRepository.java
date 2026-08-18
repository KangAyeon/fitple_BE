package com.fitple.fitple.repository;

import com.fitple.fitple.domain.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatFileRepository extends JpaRepository<ChatFile, Long> {
}