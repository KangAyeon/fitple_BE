package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.Project;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectResponse {

    private final Long id;
    private final String name;
    private final String iconUrl;
    private final boolean recruiting;
    private final LocalDateTime createdAt;

    private ProjectResponse(
            Long id,
            String name,
            String iconUrl,
            boolean recruiting,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.recruiting = recruiting;
        this.createdAt = createdAt;
    }

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getIconUrl(),
                project.isRecruiting(),
                project.getCreatedAt()
        );
    }
}