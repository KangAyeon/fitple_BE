package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.Application;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private Long applicationId;
    private Long memberId;
    private String memberName;
    private String introText;
    private String status;

    public static ApplicationResponse from(Application application) {
        return ApplicationResponse.builder()
                .applicationId(application.getId())
                .memberId(application.getMember().getId())
                .memberName(application.getMember().getName())
                .introText(application.getIntroText())
                .status(application.getStatus().name())
                .build();
    }
}