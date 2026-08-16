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

        return """
                프로젝트명: %s

                프로젝트 설명:
                %s

                팀원:
                %s

                각 팀원에게 프로젝트에 적합한 역할(role)과
                세부 담당(detailRole)을 하나씩 배정해라.

                반드시 다음 JSON 형식으로만 답변해라.

                {
                  "members": [
                    {
                      "memberId": 1,
                      "role": "백엔드",
                      "detailRole": "API 개발"
                    }
                  ]
                }
                """.formatted(
                request.getProjectName(),
                request.getProjectDescription(),
                request.getMembers()
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