package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.ChatMessage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Long messageId;
    private Long roomId;
    private Long memberId;
    private String content;
    private LocalDateTime createdAt;
    private List<ChatFileResponse> files;

    public static ChatMessageResponse from(ChatMessage message) {

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChatRoom().getId())
                .memberId(message.getMember().getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .files(
                        message.getFiles()
                                .stream()
                                .map(ChatFileResponse::from)
                                .toList()
                )
                .build();
    }
}