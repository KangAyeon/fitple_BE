package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.Project;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProjectDetailResponse {

    private final Long id;
    private final String name;
    private final String iconUrl;
    private final boolean recruiting;
    private final LocalDateTime createdAt;
    private final List<ProjectMemberResponse> members;

    private ProjectDetailResponse(
            Long id,
            String name,
            String iconUrl,
            boolean recruiting,
            LocalDateTime createdAt,
            List<ProjectMemberResponse> members
    ) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.recruiting = recruiting;
        this.createdAt = createdAt;
        this.members = members;
    }

    public static ProjectDetailResponse from(
            Project project,
            List<ProjectMemberResponse> members
    ) {
        return new ProjectDetailResponse(
                project.getId(),
                project.getName(),
                project.getIconUrl(),
                project.isRecruiting(),
                project.getCreatedAt(),
                members
        );
    }
}