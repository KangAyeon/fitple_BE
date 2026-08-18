package com.fitple.fitple.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProfileRegenerateRequest {

    private String profileSummary;
    private List<FileRequest> usedFiles;
    private boolean editable;
}