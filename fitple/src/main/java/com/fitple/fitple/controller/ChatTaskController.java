package com.fitple.fitple.controller;

import com.fitple.fitple.dto.response.TaskResponse;
import com.fitple.fitple.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatTaskController {

    private final TaskService taskService;

    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getProjectTasks(
            @PathVariable Long projectId,
            @RequestParam Long memberId,
            @RequestParam(defaultValue = "ALL") String status
    ) {
        return ResponseEntity.ok(
                taskService.getProjectTasks(
                        projectId,
                        memberId,
                        status
                )
        );
    }
    @PatchMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<TaskResponse> completeProjectTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(
                taskService.completeProjectTask(
                        projectId,
                        taskId,
                        status
                )
        );
    }
}