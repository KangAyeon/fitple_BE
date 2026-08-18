package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.ProjectMember;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamMemberResponse {

    private Long memberId;
    private String name;
    private String role;
    private String detailRole;

    public static TeamMemberResponse from(ProjectMember projectMember) {
        return TeamMemberResponse.builder()
                .memberId(projectMember.getMember().getId())
                .name(projectMember.getMember().getName())
                .role(projectMember.getRole())
                .detailRole(projectMember.getDetailRole())
                .build();
    }
}