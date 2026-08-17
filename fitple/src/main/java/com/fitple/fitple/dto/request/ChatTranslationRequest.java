package com.fitple.fitple.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatTranslationRequest {

    @NotBlank
    private String content;

    @NotBlank
    private String targetLanguage;
}