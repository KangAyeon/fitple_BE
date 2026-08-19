package com.fitple.fitple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Google Gemini API 호출 공통 클라이언트.
 * 텍스트 전용 프롬프트, 또는 텍스트+파일(이미지/PDF)을 함께 보내는 두 가지 방식을 지원한다.
 */
@Service
@RequiredArgsConstructor
public class GeminiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    private static final String MODEL = "gemini-3.6-flash";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    /**
     * 텍스트 프롬프트만 보내서 응답 텍스트를 받는다.
     */
    public String generateText(String prompt) {
        return generate(prompt, null);
    }

    /**
     * 텍스트 프롬프트 + 파일(이미지/PDF)을 함께 보내서 응답 텍스트를 받는다.
     */
    public String generateTextWithFile(String prompt, MultipartFile file) {
        return generate(prompt, file);
    }

    private String generate(String prompt, MultipartFile file) {
        try {
            List<Map<String, Object>> parts = new ArrayList<>();
            parts.add(Map.of("text", prompt));

            if (file != null && !file.isEmpty()) {
                String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
                String mimeType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

                parts.add(Map.of(
                        "inline_data", Map.of(
                                "mime_type", mimeType,
                                "data", base64Data
                        )
                ));
            }

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of("parts", parts))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = BASE_URL + MODEL + ":generateContent?key=" + apiKey;

            String responseJson = restTemplate.postForObject(url, entity, String.class);

            JsonNode root = objectMapper.readTree(responseJson);
            return root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * Gemini 응답이 ```json ... ``` 코드블록으로 감싸져 오는 경우가 있어 이를 제거하고 순수 JSON만 추출한다.
     */
    public String extractJson(String rawText) {
        String cleaned = rawText.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("^```json", "")
                    .replaceAll("^```", "")
                    .replaceAll("```$", "")
                    .trim();
        }
        return cleaned;
    }
}