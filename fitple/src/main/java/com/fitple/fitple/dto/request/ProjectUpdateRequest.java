package com.fitple.fitple.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProjectUpdateRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 2000)
    private String introText;

    private Integer recruitCount;
    private List<String> roles;
    private LocalDate periodEnd;
    private String meetingSchedule;
    private LocalDate deadline;
    private String imageUrl;
}