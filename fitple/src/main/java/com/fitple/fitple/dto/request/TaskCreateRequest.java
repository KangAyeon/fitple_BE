package com.fitple.fitple.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TaskCreateRequest {

    @NotNull
    private Long projectId;

    @NotNull
    private Long assigneeId;

    @NotBlank
    private String title;

    @NotNull
    private LocalDate dueDate;
}