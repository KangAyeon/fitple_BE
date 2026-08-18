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
public class ProjectResponse {

    private Long projectId;
    private String title;
    private String introText;
    private Integer recruitCount;
    private List<String> roles;
    private LocalDate periodEnd;
    private String meetingSchedule;
    private LocalDate deadline;
    private Long dDay;
    private String status;
    private String imageUrl;
    private Long memberId;
    private String memberName;

    public static ProjectResponse from(Project project) {
        Long dDay = (project.getDeadline() != null)
                ? ChronoUnit.DAYS.between(LocalDate.now(), project.getDeadline())
                : null;

        List<String> roleList = (project.getRoles() == null || project.getRoles().isBlank())
                ? List.of()
                : Arrays.asList(project.getRoles().split(","));

        return ProjectResponse.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .introText(project.getIntroText())
                .recruitCount(project.getRecruitCount())
                .roles(roleList)
                .periodEnd(project.getPeriodEnd())
                .meetingSchedule(project.getMeetingSchedule())
                .deadline(project.getDeadline())
                .dDay(dDay)
                .status(project.getStatus().name())
                .imageUrl(project.getImageUrl())
                .memberId(project.getMember().getId())
                .memberName(project.getMember().getName())
                .build();
    }
}