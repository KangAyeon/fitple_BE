package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryResponse {

    private Long projectId;
    private String title;
    private List<String> roles;
    private Long dDay;
    private String status;
    private String imageUrl;

    public static ProjectSummaryResponse from(Project project) {
        Long dDay = (project.getDeadline() != null)
                ? ChronoUnit.DAYS.between(LocalDate.now(), project.getDeadline())
                : null;

        List<String> roleList = (project.getRoles() == null || project.getRoles().isBlank())
                ? List.of()
                : Arrays.asList(project.getRoles().split(","));

        return ProjectSummaryResponse.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .roles(roleList)
                .dDay(dDay)
                .status(project.getStatus().name())
                .imageUrl(project.getImageUrl())
                .build();
    }
}