package com.fitple.fitple.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ScrapListResponse {

    private List<ProjectResponse> projects;

    @Getter
    @Builder
    public static class ProjectResponse {

        private Long projectId;
        private String projectIconUrl;
        private String title;
        private List<String> recruitRoles;
        private String dDay;
        private String recruitStatus;
    }
}