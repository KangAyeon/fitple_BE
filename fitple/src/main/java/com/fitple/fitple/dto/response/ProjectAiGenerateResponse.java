package com.fitple.fitple.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAiGenerateResponse {

    private String introText;
    private Integer recruitCount;
    private List<String> roles;
    private LocalDate periodEnd;
    private String meetingSchedule;
    private LocalDate deadline;
}