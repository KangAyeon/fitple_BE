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
 * OpenAI API 호출 공통 클라이언트.
 * GeminiClient와 동일한 메서드(generateText, generateTextWithFile, extractJson)를 제공해서
 * ProjectService / ApplicationService 쪽 코드는 거의 손대지 않고 교체 가능하게 했다.
 */
@Service
@RequiredArgsConstructor
public class OpenAiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    // 이미지(비전) 입력까지 지원하는 모델. 필요하면 팀 컨벤션에 맞춰 변경 가능.
    private static final String MODEL = "gpt-4o-mini";
    private static final String URL = "https://api.openai.com/v1/chat/completions";

    /**
     * 텍스트 프롬프트만 보내서 응답 텍스트를 받는다.
     */
    public String generateText(String prompt) {
        return generate(prompt, null);
    }

    /**
     * 텍스트 프롬프트 + 이미지 파일을 함께 보내서 응답 텍스트를 받는다.
     * 주의: OpenAI Chat Completions의 vision 입력은 이미지(jpg/png 등)만 지원한다.
     * PDF 등 비이미지 파일이 들어오면 이미지 파트 없이 텍스트만으로 요청한다.
     */
    public String generateTextWithFile(String prompt, MultipartFile file) {
        return generate(prompt, file);
    }

    private String generate(String prompt, MultipartFile file) {
        try {
            List<Map<String, Object>> content = new ArrayList<>();
            content.add(Map.of("type", "text", "text", prompt));

            boolean isImage = file != null && !file.isEmpty()
                    && file.getContentType() != null
                    && file.getContentType().startsWith("image/");

            if (isImage) {
                String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
                String dataUrl = "data:" + file.getContentType() + ";base64," + base64Data;

                content.add(Map.of(
                        "type", "image_url",
                        "image_url", Map.of("url", dataUrl)
                ));
            }

            Map<String, Object> requestBody = Map.of(
                    "model", MODEL,
                    "messages", List.of(Map.of("role", "user", "content", content))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String responseJson = restTemplate.postForObject(URL, entity, String.class);

            JsonNode root = objectMapper.readTree(responseJson);
            return root.path("choices").get(0)
                    .path("message").path("content")
                    .asText();

        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * OpenAI 응답이 ```json ... ``` 코드블록으로 감싸져 오는 경우가 있어 이를 제거하고 순수 JSON만 추출한다.
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