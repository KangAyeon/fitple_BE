package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.Task;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TaskResponse {

    private Long taskId;
    private Long projectId;
    private String projectName;
    private String title;
    private Long assigneeId;
    private LocalDate dueDate;
    private String status;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .taskId(task.getId())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getTitle())
                .title(task.getTitle())
                .assigneeId(task.getAssignee().getId())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .build();
    }
}