package com.fitple.fitple.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectRoleAssignResponse {

    private Long memberId;
    private String name;
    private String role;
    private String detailRole;
}