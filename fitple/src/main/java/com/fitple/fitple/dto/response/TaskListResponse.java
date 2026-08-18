package com.fitple.fitple.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TaskListResponse {

    private List<TaskResponse> tasks;
}