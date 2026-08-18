package com.fitple.fitple.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationCreateRequest {

    // 최종 소개글 (AI로 다듬은 것이든, 직접 쓴 원본 그대로든 프론트에서 확정한 값)
    private String introText;
}