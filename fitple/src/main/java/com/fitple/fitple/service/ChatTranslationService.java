package com.fitple.fitple.service;

import com.fitple.fitple.dto.request.ChatTranslationRequest;
import com.fitple.fitple.dto.response.ChatTranslationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatTranslationService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model:gpt-5.6}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public ChatTranslationResponse translate(
            ChatTranslationRequest request
    ) {

        String prompt = """
                Translate the following text into %s.

                Rules:
                - Return only the translated text.
                - Do not add explanations.
                - Preserve the original meaning and tone.

                Text:
                %s
                """.formatted(
                request.getTargetLanguage(),
                request.getContent()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", model,
                "input", prompt
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        "https://api.openai.com/v1/responses",
                        HttpMethod.POST,
                        entity,
                        Map.class
                );

        Map responseBody = response.getBody();

        if (responseBody == null) {
            throw new IllegalStateException("OpenAI 응답이 없습니다.");
        }

        String translatedContent =
                extractOutputText(responseBody);

        return ChatTranslationResponse.builder()
                .originalContent(request.getContent())
                .translatedContent(translatedContent)
                .targetLanguage(request.getTargetLanguage())
                .build();
    }

    private String extractOutputText(Map responseBody) {

        Object outputObject = responseBody.get("output");

        if (!(outputObject instanceof java.util.List<?> outputList)) {
            throw new IllegalStateException("OpenAI 응답 형식이 올바르지 않습니다.");
        }

        for (Object outputItem : outputList) {

            if (!(outputItem instanceof Map<?, ?> outputMap)) {
                continue;
            }

            Object contentObject = outputMap.get("content");

            if (!(contentObject instanceof java.util.List<?> contentList)) {
                continue;
            }

            for (Object contentItem : contentList) {

                if (!(contentItem instanceof Map<?, ?> contentMap)) {
                    continue;
                }

                Object text = contentMap.get("text");

                if (text instanceof String textValue) {
                    return textValue;
                }
            }
        }

        throw new IllegalStateException("번역 결과를 찾을 수 없습니다.");
    }
}