package com.fitple.fitple.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectMemberCreateRequest {

    @NotNull
    private Long memberId;
}