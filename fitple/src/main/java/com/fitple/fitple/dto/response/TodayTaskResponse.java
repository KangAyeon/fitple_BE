package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayTaskResponse {

    private Long taskId;
    private Long projectId;
    private String projectName;
    private String title;
    private LocalDate dueDate;
    private Long dDay;
    private String status;

    public static TodayTaskResponse from(Task task) {
        Long dDay = ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate());

        return TodayTaskResponse.builder()
                .taskId(task.getId())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getTitle())
                .title(task.getTitle())
                .dueDate(task.getDueDate())
                .dDay(dDay)
                .status(task.getStatus())
                .build();
    }
}