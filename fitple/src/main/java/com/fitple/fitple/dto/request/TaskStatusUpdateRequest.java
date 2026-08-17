package com.fitple.fitple.dto.request;

import com.fitple.fitple.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TaskStatusUpdateRequest {

    @NotNull
    private TaskStatus status;
}