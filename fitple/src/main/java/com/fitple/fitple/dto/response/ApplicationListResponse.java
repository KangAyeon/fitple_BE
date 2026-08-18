package com.fitple.fitple.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationListResponse {

    private List<ApplicationResponse> applications;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicationResponse {

        private Long projectId;

        private String projectIconUrl;

        private String title;

        private List<String> recruitRoles;

        private String dDay;

        private String applicationStatus;
    }
}