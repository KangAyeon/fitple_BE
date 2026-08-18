package com.fitple.fitple.repository;

import com.fitple.fitple.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssigneeId(Long memberId);

    List<Task> findByAssigneeIdAndStatus(
            Long memberId,
            String status
    );

    List<Task> findByProjectId(Long projectId);

    List<Task> findByProjectIdAndDueDate(
            Long projectId,
            LocalDate dueDate
    );
    List<Task> findByProjectIdAndDueDateAndStatus(
            Long projectId,
            LocalDate dueDate,
            String status
    );
    List<Task> findByProjectIdAndAssigneeId(
            Long projectId,
            Long memberId
    );

    List<Task> findByProjectIdAndAssigneeIdAndStatus(
            Long projectId,
            Long memberId,
            String status
    );

}