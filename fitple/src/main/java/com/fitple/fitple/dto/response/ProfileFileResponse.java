package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.ProfileFile;
import lombok.Getter;

@Getter
public class ProfileFileResponse {

    private final Long fileId;
    private final String fileUrl;
    private final String originalName;

    public ProfileFileResponse(ProfileFile file) {
        this.fileId = file.getId();
        this.fileUrl = file.getFileUrl();
        this.originalName = file.getOriginalName();
    }
}