package com.fitple.fitple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitple.fitple.domain.Project;
import com.fitple.fitple.domain.ProjectMember;
import com.fitple.fitple.domain.Task;
import com.fitple.fitple.domain.TaskStatus;
import com.fitple.fitple.dto.response.TaskResponse;
import com.fitple.fitple.repository.ChatMessageRepository;
import com.fitple.fitple.repository.ProjectMemberRepository;
import com.fitple.fitple.repository.ProjectRepository;
import com.fitple.fitple.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fitple.fitple.domain.ChatMessage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AITaskService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<TaskResponse> generateTodayTasks(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 프로젝트입니다."
                        ));

        LocalDate today = LocalDate.now();

        List<Task> existingTasks =
                taskRepository.findByProjectIdAndDueDate(
                        projectId,
                        today
                );

        if (!existingTasks.isEmpty()) {
            return existingTasks.stream()
                    .map(TaskResponse::from)
                    .toList();
        }

        List<ProjectMember> projectMembers =
                projectMemberRepository.findByProjectId(projectId);

        if (projectMembers.isEmpty()) {
            throw new IllegalStateException(
                    "프로젝트에 참여한 팀원이 없습니다."
            );
        }

        List<ChatMessage> chatMessages =
                chatMessageRepository
                        .findByChatRoomProjectIdOrderByCreatedAtAsc(projectId);

        System.out.println("=== AI 과제 생성용 채팅 기록 ===");

        for (ChatMessage message : chatMessages) {
            System.out.println(
                    message.getMember().getName()
                            + ": "
                            + message.getContent()
            );
        }

        System.out.println("=== 채팅 기록 개수: "
                + chatMessages.size()
                + " ===");

        String prompt = createPrompt(
                project,
                projectMembers,
                chatMessages,
                today
        );

        String responseBody = requestOpenAI(prompt);

        return saveTasks(
                project,
                projectMembers,
                responseBody,
                today
        );
    }

    private String createPrompt(
            Project project,
            List<ProjectMember> projectMembers,
            List<ChatMessage> chatMessages,
            LocalDate today
    ) {

        String membersJson;

        try {
            membersJson = objectMapper.writeValueAsString(
                    projectMembers.stream()
                            .map(projectMember -> new MemberInfo(
                                    projectMember.getMember().getId(),
                                    projectMember.getMember().getName(),
                                    projectMember.getRole(),
                                    projectMember.getDetailRole()
                            ))
                            .toList()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "팀원 정보 생성에 실패했습니다.",
                    e
            );
        }

        String chatHistory = chatMessages.stream()
                .map(message ->
                        message.getMember().getName()
                                + ": "
                                + message.getContent()
                )
                .collect(Collectors.joining("\n"));

        return """
            프로젝트명: %s

            프로젝트 설명:
            %s

            오늘 날짜:
            %s

            팀원:
            %s

            지금까지의 프로젝트 채팅 내용:
            %s

            위 프로젝트 정보, 팀원 정보, 그리고 지금까지의 채팅 내용을 종합하여
            현재 프로젝트의 진행 상황을 파악하라.

            특히 채팅에서:
            - 이미 완료된 작업
            - 진행 중인 작업
            - 아직 해결되지 않은 문제
            - 다음에 해야 한다고 언급된 작업
            을 확인하라.

            이미 완료되었다고 확인된 작업은 오늘의 과제로 다시 배정하지 마라.

            채팅에서 아직 완료되지 않았거나
            추가 작업이 필요하다고 판단되는 내용을 우선적으로 과제로 생성하라.

            각 팀원이 오늘 수행하기 적절한 과제를 하나씩 생성하라.

            매우 중요한 규칙:
            1. memberId는 반드시 위 팀원 목록에 존재하는 값을 그대로 사용하라.
            2. 새로운 memberId를 생성하거나 추측하지 마라.
            3. 모든 팀원에게 정확히 하나의 과제를 배정하라.
            4. 과제는 해당 팀원의 role과 detailRole에 적합해야 한다.
            5. 과제는 오늘 수행할 수 있는 구체적인 작업이어야 한다.
            6. 과제 제목은 짧고 명확하게 작성하라.
            7. dueDate는 반드시 오늘 날짜인 %s를 사용하라.
            8. status는 반드시 "TODO"로 작성하라.

            반드시 JSON 객체만 반환하시오.
            Markdown 코드 블록을 사용하지 마시오.
            설명이나 추가 문장을 작성하지 마시오.

            반드시 다음 형식의 순수 JSON으로만 응답하시오.

            {
              "tasks": [
                {
                  "memberId": 1,
                  "title": "로그인 API 구현",
                  "dueDate": "%s",
                  "status": "TODO"
                }
              ]
            }
            """.formatted(
                project.getName(),
                project.getDescription(),
                today,
                membersJson,
                chatHistory,
                today,
                today
        );
    }

    private String requestOpenAI(String prompt) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String promptJson;

        try {
            promptJson =
                    objectMapper.writeValueAsString(prompt);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "AI 요청 생성에 실패했습니다.",
                    e
            );
        }

        String body = """
                {
                  "model": "%s",
                  "messages": [
                    {
                      "role": "system",
                      "content": "너는 프로젝트 팀원의 오늘 할 일을 생성하는 AI다. 반드시 JSON 형식으로만 답변한다."
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

        HttpEntity<String> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        "https://api.openai.com/v1/chat/completions",
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        System.out.println("=== AI 과제 생성 응답 ===");
        System.out.println(response.getBody());

        return response.getBody();
    }

    private List<TaskResponse> saveTasks(
            Project project,
            List<ProjectMember> projectMembers,
            String responseBody,
            LocalDate today
    ) {

        try {
            JsonNode root =
                    objectMapper.readTree(responseBody);

            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText()
                    .trim();

            if (content.startsWith("```json")) {
                content = content.substring(7);
            }

            if (content.endsWith("```")) {
                content = content.substring(
                        0,
                        content.length() - 3
                );
            }

            content = content.trim();

            JsonNode tasksNode =
                    objectMapper
                            .readTree(content)
                            .path("tasks");

            List<AITaskData> aiTasks =
                    objectMapper.readerForListOf(
                            AITaskData.class
                    ).readValue(
                            tasksNode.toString()
                    );

            List<Task> tasks = aiTasks.stream()
                    .map(aiTask -> {

                        ProjectMember projectMember =
                                projectMembers.stream()
                                        .filter(pm ->
                                                pm.getMember()
                                                        .getId()
                                                        .equals(
                                                                aiTask.memberId()
                                                        )
                                        )
                                        .findFirst()
                                        .orElseThrow(() ->
                                                new IllegalArgumentException(
                                                        "AI가 존재하지 않는 팀원을 지정했습니다."
                                                )
                                        );

                        return Task.builder()
                                .project(project)
                                .assignee(projectMember.getMember())
                                .title(aiTask.title())
                                .dueDate(today)
                                .status(TaskStatus.TODO.name())
                                .build();
                    })
                    .toList();

            List<Task> savedTasks =
                    taskRepository.saveAll(tasks);

            return savedTasks.stream()
                    .map(TaskResponse::from)
                    .toList();

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "AI 과제 생성 결과를 처리하지 못했습니다.",
                    e
            );
        }
    }

    private record MemberInfo(
            Long memberId,
            String name,
            String role,
            String detailRole
    ) {
    }

    private record AITaskData(
            Long memberId,
            String title,
            String dueDate,
            String status
    ) {
    }
}