package com.fitple.fitple.repository;

import com.fitple.fitple.domain.MeetingMinute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingMinuteRepository
        extends JpaRepository<MeetingMinute, Long> {

    List<MeetingMinute> findByProjectIdOrderByCreatedAtDesc(
            Long projectId
    );
}