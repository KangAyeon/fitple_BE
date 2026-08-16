package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.ProjectMember;
import lombok.Getter;

@Getter
public class ProjectMemberResponse {

    private final Long memberId;
    private final String name;
    private final String role;
    private final String detailRole;

    private ProjectMemberResponse(
            Long memberId,
            String name,
            String role,
            String detailRole
    ) {
        this.memberId = memberId;
        this.name = name;
        this.role = role;
        this.detailRole = detailRole;
    }

    public static ProjectMemberResponse from(ProjectMember projectMember) {
        return new ProjectMemberResponse(
                projectMember.getMember().getId(),
                projectMember.getMember().getName(),
                projectMember.getRole(),
                projectMember.getDetailRole()
        );
    }
}