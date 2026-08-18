package com.fitple.fitple.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleResponse {

    private Long memberId;
    private String name;
    private String role;
    private String reason;
}