package com.fitple.fitple.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ScheduleUpdateRequest {

    private Long stageId;

    private LocalDate previousStartDate;
    private LocalDate previousEndDate;

    private LocalDate newStartDate;
    private LocalDate newEndDate;

    private String reason;
}