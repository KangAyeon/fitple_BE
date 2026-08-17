package com.fitple.fitple.repository;

import com.fitple.fitple.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssigneeId(Long memberId);

    List<Task> findByAssigneeIdAndStatus(
            Long memberId,
            String status
    );

    List<Task> findByProjectId(Long projectId);
}