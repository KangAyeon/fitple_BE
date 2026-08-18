package com.fitple.fitple.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageResponse {

    private Long memberId;
    private String name;
    private String loginId;
}