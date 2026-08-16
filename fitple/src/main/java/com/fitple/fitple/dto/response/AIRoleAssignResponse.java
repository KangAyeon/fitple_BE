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
public class AIRoleAssignResponse {

    private Long projectId;
    private List<MemberRole> members;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberRole {
        private Long memberId;
        private String role;
        private String detailRole;
    }
}