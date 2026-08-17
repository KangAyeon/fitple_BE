package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Long messageId;
    private Long roomId;
    private Long memberId;
    private String memberName;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage message) {

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChatRoom().getId())
                .memberId(message.getMember().getId())
                .memberName(message.getMember().getName())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}