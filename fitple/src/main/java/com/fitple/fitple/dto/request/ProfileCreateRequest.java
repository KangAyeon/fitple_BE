package com.fitple.fitple.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProfileCreateRequest {

    private String name;
    private String bio;
    private List<FileRequest> usedFiles; // FileRequest DTO 리스트 사용
}