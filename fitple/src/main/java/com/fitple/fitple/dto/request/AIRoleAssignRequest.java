package com.fitple.fitple.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AIRoleAssignRequest {

    private Long projectId;
    private String projectName;
    private String projectDescription;
    private List<MemberInfo> members;

    @Getter
    @Builder
    public static class MemberInfo {
        private Long memberId;
        private String name;
    }
}