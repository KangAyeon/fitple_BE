package com.fitple.fitple.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ProjectUpdateRequest {

    private String title;
    private String introText;
    private Integer recruitCount;
    private List<String> roles;
    private LocalDate periodEnd;
    private String meetingSchedule;
    private LocalDate deadline;
    private String imageUrl;
}