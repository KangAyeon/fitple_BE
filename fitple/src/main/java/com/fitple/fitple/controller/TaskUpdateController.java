package com.fitple.fitple.controller;

import com.fitple.fitple.dto.request.TaskStatusUpdateRequest;
import com.fitple.fitple.dto.response.TaskResponse;
import com.fitple.fitple.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskUpdateController {

    private final TaskService taskService;

    @PatchMapping("/{taskId}")
    public TaskResponse updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request
    ) {
        return taskService.updateTaskStatus(taskId, request);
    }
}