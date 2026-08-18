package com.fitple.fitple.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProjectAiGenerateRequest {

    private String title;

    private String rawIntroText;

    // 선택 (없을 수 있음)
    private MultipartFile file;
}