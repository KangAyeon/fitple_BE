package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.ProjectMember;
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
public class ProjectMyResponse {

    private Long projectId;
    private String title;
    private String myRole;
    private Long dDay;
    private String status;

    public static ProjectMyResponse from(ProjectMember projectMember) {
        var project = projectMember.getProject();

        Long dDay = (project.getDeadline() != null)
                ? ChronoUnit.DAYS.between(LocalDate.now(), project.getDeadline())
                : null;

        return ProjectMyResponse.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .myRole(projectMember.getRole())
                .dDay(dDay)
                .status(project.getStatus().name())
                .build();
    }
}