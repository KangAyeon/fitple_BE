package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatSettingResponse {

    private Long memberId;
    private boolean translationEnabled;

    public static ChatSettingResponse from(Member member) {
        return ChatSettingResponse.builder()
                .memberId(member.getId())
                .translationEnabled(member.isTranslationEnabled())
                .build();
    }
}