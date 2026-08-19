package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.Application;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/**
 * GET /api/applications/my 전용 응답 DTO.
 * 필드명은 프론트가 요청한 명세를 그대로 따른다 (projectTitle, dday 등 기존 표기와 다름에 주의).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationMyResponse {

    private Long applicationId;
    private Long projectId;
    private String projectTitle;
    private List<String> roles;
    private String projectStatus;
    private String imageUrl;
    private Long dday;
    private String status;
    private LocalDate appliedAt;

    public static ApplicationMyResponse from(Application application) {
        var project = application.getProject();

        Long dday = (project.getDeadline() != null)
                ? ChronoUnit.DAYS.between(LocalDate.now(), project.getDeadline())
                : null;

        List<String> roleList = (project.getRoles() == null || project.getRoles().isBlank())
                ? List.of()
                : Arrays.asList(project.getRoles().split(","));

        LocalDateTime createdAt = application.getCreatedAt();

        return ApplicationMyResponse.builder()
                .applicationId(application.getId())
                .projectId(project.getId())
                .projectTitle(project.getTitle())
                .roles(roleList)
                .projectStatus(project.getStatus().name())
                .imageUrl(project.getImageUrl())
                .dday(dday)
                .status(application.getStatus().name())
                .appliedAt(createdAt != null ? createdAt.toLocalDate() : null)
                .build();
    }
}