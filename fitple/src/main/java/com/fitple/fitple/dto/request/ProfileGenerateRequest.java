package com.fitple.fitple.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProfileGenerateRequest {

    private String profileSummary;
    private List<FileRequest> usedFiles; // String 리스트 -> FileRequest 리스트로 변경
    private boolean editable;
}