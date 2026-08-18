package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.ChatFile;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatFileResponse {

    private Long fileId;
    private String originalFileName;
    private String fileUrl;
    private String contentType;
    private Long fileSize;

    public static ChatFileResponse from(ChatFile file) {
        return ChatFileResponse.builder()
                .fileId(file.getId())
                .originalFileName(file.getOriginalFileName())
                .fileUrl(file.getFileUrl())
                .contentType(file.getContentType())
                .fileSize(file.getFileSize())
                .build();
    }
}