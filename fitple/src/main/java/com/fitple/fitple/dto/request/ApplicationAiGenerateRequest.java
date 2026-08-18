package com.fitple.fitple.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationAiGenerateRequest {

    // 사용자가 직접 작성한 소개글 원본 (필수)
    private String rawIntroText;
}