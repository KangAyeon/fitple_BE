package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.ChatFile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatFileResponse {

    private Long fileId;
    private String originalFileName;
    private String fileUrl;
    private String contentType;
    private Long fileSize;
    private Long memberId;
    private String memberName;

    public static ChatFileResponse from(ChatFile chatFile) {
        return ChatFileResponse.builder()
                .fileId(chatFile.getId())
                .originalFileName(chatFile.getOriginalFileName())
                .fileUrl(chatFile.getFileUrl())
                .contentType(chatFile.getContentType())
                .fileSize(chatFile.getFileSize())
                .memberId(chatFile.getChatMessage().getMember().getId())
                .memberName(chatFile.getChatMessage().getMember().getName())
                .build();
    }
}