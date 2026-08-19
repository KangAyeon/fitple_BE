package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.Project;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProjectDetailResponse {

    private final Long id;
    private final String title;
    private final String imageUrl;
    private final String status;
    private final LocalDateTime createdAt;
    private final List<ProjectMemberResponse> members;

    private ProjectDetailResponse(
            Long id,
            String title,
            String imageUrl,
            String status,
            LocalDateTime createdAt,
            List<ProjectMemberResponse> members
    ) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.members = members;
    }

    public static ProjectDetailResponse from(
            Project project,
            List<ProjectMemberResponse> members
    ) {
        return new ProjectDetailResponse(
                project.getId(),
                project.getTitle(),
                project.getImageUrl(),
                project.getStatus().name(),
                project.getCreatedAt(),
                members
        );
    }
}