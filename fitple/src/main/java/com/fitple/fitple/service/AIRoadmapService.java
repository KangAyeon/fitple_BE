package com.fitple.fitple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitple.fitple.domain.ChatMessage;
import com.fitple.fitple.domain.ChatRoom;
import com.fitple.fitple.domain.Member;
import com.fitple.fitple.domain.Project;
import com.fitple.fitple.domain.ProjectMember;
import com.fitple.fitple.domain.RoadmapStage;
import com.fitple.fitple.dto.response.*;
import com.fitple.fitple.repository.ChatMessageRepository;
import com.fitple.fitple.repository.ChatRoomRepository;
import com.fitple.fitple.repository.ProjectMemberRepository;
import com.fitple.fitple.repository.RoadmapStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fitple.fitple.dto.request.ScheduleAIUpdateRequest;
import com.fitple.fitple.dto.request.ScheduleUpdateRequest;
import com.fitple.fitple.dto.response.RoadmapResponse;
import com.fitple.fitple.dto.response.RoadmapStepResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.fitple.fitple.dto.response.ScheduleUpdateResponse;

@Service
@RequiredArgsConstructor
public class AIRoadmapService {

    private final ChatRoomRepository chatRoomRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RoadmapStageRepository roadmapStageRepository;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public List<RoadmapStageResponse> generateRoadmap(Long roomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 채팅방입니다."
                        ));

        Project project = chatRoom.getProject();

        List<ProjectMember> projectMembers =
                projectMemberRepository.findByProjectId(
                        project.getId()
                );

        if (projectMembers.isEmpty()) {
            throw new IllegalStateException(
                    "프로젝트에 참여한 팀원이 없습니다."
            );
        }

        List<ChatMessage> chatMessages =
                chatMessageRepository
                        .findByChatRoomProjectIdOrderByCreatedAtAsc(
                                project.getId()
                        );

        LocalDate today = LocalDate.now();

        String prompt = createPrompt(
                project,
                projectMembers,
                chatMessages,
                today
        );

        String responseBody = requestOpenAI(prompt);

        return saveRoadmap(
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
                            .map(pm -> new MemberInfo(
                                    pm.getMember().getId(),
                                    pm.getMember().getName(),
                                    pm.getRole(),
                                    pm.getDetailRole()
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
                프로젝트명:
                %s

                프로젝트 설명:
                %s

                오늘 날짜:
                %s

                팀원:
                %s

                프로젝트 채팅 기록:
                %s

                위 정보를 종합하여 프로젝트 전체 로드맵을 생성하라.

                요구사항:
                1. 프로젝트의 시작부터 최종 완료까지 필요한 단계를 생성하라.
                2. 단계는 실제 프로젝트 진행 순서에 맞아야 한다.
                3. 각 단계에는 하나 이상의 담당 팀원을 배정하라.
                4. 담당자는 반드시 위 팀원 목록의 memberId만 사용하라.
                5. 팀원의 role과 detailRole을 고려하여 담당자를 배정하라.
                6. 각 단계는 구체적인 작업 단위여야 한다.
                7. 단계별 시작일과 종료일을 설정하라.
                8. 시작일은 종료일보다 늦을 수 없다.
                9. 단계 사이에 불필요한 공백을 만들지 마라.
                10. 오늘 날짜를 기준으로 현실적인 일정을 생성하라.
                11. 모든 날짜는 yyyy-MM-dd 형식으로 작성하라.
                12. stageNumber는 1부터 순서대로 증가시켜라.

                반드시 다음 JSON 형식만 반환하라.

                {
                  "stages": [
                    {
                      "stageNumber": 1,
                      "title": "기획",
                      "description": "프로젝트의 주요 기능과 방향을 정의한다.",
                      "memberIds": [1, 2],
                      "startDate": "2026-08-18",
                      "endDate": "2026-08-20"
                    }
                  ]
                }

                Markdown 코드 블록이나 설명 문장을 절대 작성하지 마라.
                """.formatted(
                project.getTitle(),
                project.getIntroText(),
                today,
                membersJson,
                chatHistory
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
                      "content": "너는 프로젝트 로드맵을 생성하는 AI다. 반드시 JSON 형식으로만 답변한다."
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

        System.out.println("=== AI 로드맵 생성 응답 ===");
        System.out.println(response.getBody());

        return response.getBody();
    }

    private List<RoadmapStageResponse> saveRoadmap(
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

            JsonNode stagesNode =
                    objectMapper.readTree(content)
                            .path("stages");

            List<AIStageData> aiStages =
                    objectMapper.readerForListOf(
                            AIStageData.class
                    ).readValue(stagesNode.toString());

            int nextVersion = roadmapStageRepository
                    .findByProjectIdOrderByRoadmapVersionDescStageNumberAsc(
                            project.getId()
                    )
                    .stream()
                    .findFirst()
                    .map(RoadmapStage::getRoadmapVersion)
                    .orElse(0) + 1;

            List<RoadmapStage> stages =
                    aiStages.stream()
                            .map(aiStage -> {

                                List<Member> assignees =
                                        aiStage.memberIds()
                                                .stream()
                                                .map(memberId ->
                                                        projectMembers.stream()
                                                                .filter(pm ->
                                                                        pm.getMember()
                                                                                .getId()
                                                                                .equals(memberId)
                                                                )
                                                                .findFirst()
                                                                .orElseThrow(() ->
                                                                        new IllegalArgumentException(
                                                                                "AI가 존재하지 않는 팀원을 지정했습니다."
                                                                        )
                                                                )
                                                                .getMember()
                                                )
                                                .toList();

                                return RoadmapStage.builder()
                                        .project(project)
                                        .roadmapVersion(nextVersion)
                                        .stageNumber(aiStage.stageNumber())
                                        .title(aiStage.title())
                                        .description(aiStage.description())
                                        .startDate(
                                                LocalDate.parse(
                                                        aiStage.startDate()
                                                )
                                        )
                                        .endDate(
                                                LocalDate.parse(
                                                        aiStage.endDate()
                                                )
                                        )
                                        .assignees(assignees)
                                        .build();
                            })
                            .toList();

            List<RoadmapStage> savedStages =
                    roadmapStageRepository.saveAll(stages);

            return savedStages.stream()
                    .map(stage ->
                            RoadmapStageResponse.from(
                                    stage,
                                    today
                            )
                    )
                    .toList();

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "AI 로드맵 생성 결과를 처리하지 못했습니다.",
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

    private record AIStageData(
            Integer stageNumber,
            String title,
            String description,
            List<Long> memberIds,
            String startDate,
            String endDate
    ) {
    }
//    @Transactional(readOnly = true)
//    public RoadmapResponse getRoadmap(Long projectId) {
//
//        List<RoadmapStage> stages =
//                roadmapStageRepository
//                        .findByProjectIdOrderByRoadmapVersionDescStageNumberAsc(
//                                projectId
//                        );
//
//        if (stages.isEmpty()) {
//            throw new IllegalArgumentException(
//                    "존재하는 로드맵이 없습니다."
//            );
//        }
//
//        Integer latestVersion =
//                stages.get(0).getRoadmapVersion();
//
//        List<RoadmapStageResponse> steps =
//                stages.stream()
//                        .filter(stage ->
//                                stage.getRoadmapVersion()
//                                        .equals(latestVersion)
//                        )
//                        .map(stage ->
//                                RoadmapStageResponse.from(
//                                        stage,
//                                        LocalDate.now()
//                                )
//                        )
//                        .toList();
//
//        return RoadmapResponse.builder()
//                .roadmapVersion(latestVersion)
//                .steps(steps)
//                .build();
//    }
    @Transactional
    public RoadmapResponse updateSchedule(
            Long roomId,
            ScheduleAIUpdateRequest request
    ) {

        ChatRoom chatRoom =
                chatRoomRepository.findById(roomId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 채팅방입니다."
                                ));

        Long projectId =
                chatRoom.getProject().getId();

        if (request.getUpdates() == null ||
                request.getUpdates().isEmpty()) {

            throw new IllegalArgumentException(
                    "업데이트할 일정이 없습니다."
            );
        }

        for (ScheduleUpdateRequest update :
                request.getUpdates()) {

            RoadmapStage stage =
                    roadmapStageRepository
                            .findById(update.getStageId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "존재하지 않는 로드맵 단계입니다."
                                    ));

            // 다른 프로젝트의 stage를 수정하지 못하도록 확인
            if (!stage.getProject()
                    .getId()
                    .equals(projectId)) {

                throw new IllegalArgumentException(
                        "현재 프로젝트의 로드맵 단계가 아닙니다."
                );
            }

            // 기존 일정이 실제 DB 값과 일치하는지 확인
            if (!stage.getStartDate()
                    .equals(update.getPreviousStartDate())
                    ||
                    !stage.getEndDate()
                            .equals(update.getPreviousEndDate())) {

                throw new IllegalArgumentException(
                        "기존 일정이 현재 로드맵과 일치하지 않습니다."
                );
            }

            if (update.getNewStartDate() == null ||
                    update.getNewEndDate() == null) {

                throw new IllegalArgumentException(
                        "변경할 일정이 없습니다."
                );
            }

            if (update.getNewStartDate()
                    .isAfter(update.getNewEndDate())) {

                throw new IllegalArgumentException(
                        "시작일은 종료일보다 늦을 수 없습니다."
                );
            }

            stage.setStartDate(
                    update.getNewStartDate()
            );

            stage.setEndDate(
                    update.getNewEndDate()
            );
        }

        return getRoadmap(projectId);
    }
    @Transactional
    public List<ScheduleUpdateResponse> updateSchedule(
            List<ScheduleUpdateRequest> updates
    ) {

        return updates.stream()
                .map(update -> {

                    RoadmapStage stage =
                            roadmapStageRepository.findById(update.getStageId())
                                    .orElseThrow(() ->
                                            new IllegalArgumentException(
                                                    "존재하지 않는 로드맵 단계입니다."
                                            ));

                    if (!stage.getStartDate()
                            .equals(update.getPreviousStartDate())
                            || !stage.getEndDate()
                            .equals(update.getPreviousEndDate())) {

                        throw new IllegalArgumentException(
                                "현재 로드맵 일정과 이전 일정이 일치하지 않습니다."
                        );
                    }

                    if (update.getNewStartDate()
                            .isAfter(update.getNewEndDate())) {

                        throw new IllegalArgumentException(
                                "시작일은 종료일보다 늦을 수 없습니다."
                        );
                    }

                    LocalDate previousStartDate =
                            stage.getStartDate();

                    LocalDate previousEndDate =
                            stage.getEndDate();

                    stage.setStartDate(
                            update.getNewStartDate()
                    );

                    stage.setEndDate(
                            update.getNewEndDate()
                    );

                    return ScheduleUpdateResponse.of(
                            stage.getId(),
                            previousStartDate,
                            previousEndDate,
                            stage,
                            update.getReason()
                    );
                })
                .toList();
    }
    @Transactional(readOnly = true)
    public RoadmapResponse getRoadmap(Long projectId) {

        List<RoadmapStage> stages =
                roadmapStageRepository
                        .findByProjectIdOrderByRoadmapVersionDescStageNumberAsc(
                                projectId
                        );

        if (stages.isEmpty()) {
            throw new IllegalArgumentException(
                    "존재하는 로드맵이 없습니다."
            );
        }

        Integer latestVersion =
                stages.get(0).getRoadmapVersion();

        List<RoadmapStage> latestStages =
                roadmapStageRepository
                        .findByProjectIdAndRoadmapVersionOrderByStageNumberAsc(
                                projectId,
                                latestVersion
                        );

        List<RoadmapStageResponse> steps =
                latestStages.stream()
                        .map(stage ->
                                RoadmapStageResponse.from(
                                        stage,
                                        LocalDate.now()
                                )
                        )
                        .toList();

        return RoadmapResponse.builder()
                .roadmapVersion(latestVersion)
                .steps(steps)
                .build();
    }
}