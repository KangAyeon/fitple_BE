package com.fitple.fitple.controller;

import com.fitple.fitple.dto.request.TaskCreateRequest;
import com.fitple.fitple.dto.response.TaskResponse;
import com.fitple.fitple.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(request));
    }

    @GetMapping("/member/{memberId}")
    public List<TaskResponse> getMyTasks(
            @PathVariable Long memberId,
            @RequestParam(required = false) String status
    ) {
        return taskService.getMyTasks(memberId, status);
    }
}