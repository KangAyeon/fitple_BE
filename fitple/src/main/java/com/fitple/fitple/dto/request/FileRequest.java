package com.fitple.fitple.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileRequest {

    private Long fileId;        // 파일 PK
    private String fileUrl;     // 파일 접근 URL
    private String originalName;// 원본 파일명
}