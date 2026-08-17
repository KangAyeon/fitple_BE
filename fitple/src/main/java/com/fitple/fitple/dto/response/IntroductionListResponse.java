package com.fitple.fitple.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroductionListResponse {

    private List<IntroductionResponse> introductions;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IntroductionResponse {

        private Long introductionId;
        private String title;
        private String content;
    }
}
