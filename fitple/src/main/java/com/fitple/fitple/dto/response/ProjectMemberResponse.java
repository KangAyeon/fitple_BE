package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.ProjectMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberResponse {

    private Long memberId;
    private String name;
    private String role;
    private String detailRole;

    public static ProjectMemberResponse from(ProjectMember projectMember) {
        return ProjectMemberResponse.builder()
                .memberId(projectMember.getMember().getId())
                .name(projectMember.getMember().getName())
                .role(projectMember.getRole())
                .detailRole(projectMember.getDetailRole())
                .build();
    }
}