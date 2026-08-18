package com.fitple.fitple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitple.fitple.domain.Project;
import com.fitple.fitple.domain.ProjectMember;
import com.fitple.fitple.dto.request.AIRoleAssignRequest;
import com.fitple.fitple.dto.response.AIRoleAssignResponse;
import com.fitple.fitple.repository.ProjectMemberRepository;
import com.fitple.fitple.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIRoleService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIRoleAssignResponse assignRoles(Long projectId) {

        AIRoleAssignRequest request = createRoleAssignRequest(projectId);

        String prompt = createPrompt(request);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String promptJson;

        try {
            promptJson = objectMapper.writeValueAsString(prompt);
        } catch (Exception e) {
            throw new IllegalArgumentException("AI 요청 생성에 실패했습니다.", e);
        }

        String body = """
        {
          "model": "%s",
          "messages": [
            {
              "role": "system",
              "content": "너는 프로젝트 팀원의 역할을 배정하는 AI다. 반드시 JSON 형식으로만 답변한다."
            },
            {
              "role": "user",
              "content": %s
            }
          ]
        }
        """.formatted(
                model,
                promptJson
        );

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
        );
        // 로그 확인 용으로 작성한,,
        System.out.println("=== AI 응답 ===");
        System.out.println(response.getBody());

        AIRoleAssignResponse result =
                parseResponse(projectId, response.getBody());

        for (AIRoleAssignResponse.MemberRole memberRole : result.getMembers()) {

            ProjectMember projectMember =
                    projectMemberRepository.findByProjectIdAndMemberId(
                            projectId,
                            memberRole.getMemberId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException("프로젝트 참여자를 찾을 수 없습니다.")
                    );

            projectMember.setRole(memberRole.getRole());
            projectMember.setDetailRole(memberRole.getDetailRole());

            projectMemberRepository.save(projectMember);
        }

        return result;
    }

    private String createPrompt(AIRoleAssignRequest request) {

        String membersJson;

        try {
            membersJson = objectMapper.writeValueAsString(request.getMembers());
        } catch (Exception e) {
            throw new IllegalArgumentException("팀원 정보 생성에 실패했습니다.", e);
        }

        return """
            프로젝트명: %s

            프로젝트 설명:
            %s

            팀원:
            %s

            위 팀원 목록에 존재하는 모든 팀원에게
            프로젝트에 적합한 역할(role)과
            세부 담당(detailRole)을 하나씩 배정하라.

            매우 중요한 규칙:
            1. memberId는 반드시 위 팀원 목록에 존재하는 값을 그대로 사용하라.
            2. 새로운 memberId를 생성하거나 추측하지 마라.
            3. 위 목록에 없는 memberId를 절대 사용하지 마라.
            4. 입력된 팀원 수와 동일한 수의 팀원을 반환하라.
            5. 각 팀원은 반드시 한 번씩만 반환하라.

            반드시 JSON 객체만 반환하시오.

            Markdown 코드 블록(```json ... ```)형식을 사용하지 마시오.
            설명이나 추가 문장도 작성하지 마시오.

            반드시 다음 형식의 순수 JSON으로만 응답하시오.
            변수명은 고정하고 값만 변경하시오.

            {
              "members": [
                {
                  "memberId": 1,
                  "role": "프론트엔드",
                  "detailRole": "UI/UX 디자인"
                }
              ]
            }
            """.formatted(
                request.getProjectName(),
                request.getProjectDescription(),
                membersJson
        );
    }

    private AIRoleAssignResponse parseResponse(
            Long projectId,
            String responseBody
    ) {

        try {
            JsonNode root = objectMapper.readTree(responseBody);

            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            content = content.trim();

            if (content.startsWith("```json")) {
                content = content.substring(7);
            }

            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3);
            }

            content = content.trim();

            JsonNode result = objectMapper.readTree(content);

            List<AIRoleAssignResponse.MemberRole> members =
                    objectMapper.readerForListOf(
                            AIRoleAssignResponse.MemberRole.class
                    ).readValue(
                            result.path("members").toString()
                    );

            return AIRoleAssignResponse.builder()
                    .projectId(projectId)
                    .members(members)
                    .build();

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "AI 역할 배정 결과를 처리하지 못했습니다.", e
            );
        }
    }

    // 기존 메서드
    public AIRoleAssignRequest createRoleAssignRequest(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        List<AIRoleAssignRequest.MemberInfo> members =
                projectMemberRepository.findByProjectId(projectId)
                        .stream()
                        .map(this::toMemberInfo)
                        .toList();

        return AIRoleAssignRequest.builder()
                .projectId(project.getId())
                .projectName(project.getName())
                .projectDescription(project.getDescription())
                .members(members)
                .build();
    }

    private AIRoleAssignRequest.MemberInfo toMemberInfo(
            ProjectMember projectMember
    ) {
        return AIRoleAssignRequest.MemberInfo.builder()
                .memberId(projectMember.getMember().getId())
                .name(projectMember.getMember().getName())
                .build();
    }
}