package com.fitple.fitple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitple.fitple.domain.ChatRoom;
import com.fitple.fitple.domain.Member;
import com.fitple.fitple.domain.Project;
import com.fitple.fitple.domain.ProjectMember;
import com.fitple.fitple.dto.request.ProjectAiGenerateRequest;
import com.fitple.fitple.dto.request.ProjectCreateRequest;
import com.fitple.fitple.dto.request.ProjectUpdateRequest;
import com.fitple.fitple.dto.response.AssignRoleResponse;
import com.fitple.fitple.dto.response.ProjectAiGenerateResponse;
import com.fitple.fitple.dto.response.ProjectCreateResponse;
import com.fitple.fitple.dto.response.ProjectMemberResponse;
import com.fitple.fitple.dto.response.ProjectMyResponse;
import com.fitple.fitple.dto.response.ProjectResponse;
import com.fitple.fitple.dto.response.ProjectSummaryResponse;
import com.fitple.fitple.repository.ChatRoomRepository;
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.repository.ProjectMemberRepository;
import com.fitple.fitple.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final QrCodeService qrCodeService;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    /**
     * 사용자가 직접 쓴 소개글(+파일이 있으면 파일)을 참고해서
     * AI(Gemini)가 최종 소개글과 구조화된 정보를 생성한다.
     */
    public ProjectAiGenerateResponse generateIntro(ProjectAiGenerateRequest request) {
        String todayText = LocalDate.now().toString();

        String prompt = """
                너는 대학생 프로젝트 팀원 모집 게시글을 작성하는 도우미야.
                오늘 날짜는 %s야. 날짜를 추론할 때 반드시 이 기준으로 판단해줘.
                (예: "9월 30일까지"처럼 연도가 없는 표현은 오늘 날짜 기준 가장 가까운 미래의 9월 30일로 해석해줘.
                이미 지난 날짜라면 내년으로 해석해줘.)

                아래는 사용자가 직접 작성한 프로젝트 소개글 초안이야.

                제목: %s
                초안: %s

                이 내용을 참고해서(첨부된 파일이 있다면 그 내용도 함께 참고해서),
                다른 사람이 읽기 좋은 완성된 소개글과, 아래 정보를 구조화해서
                **오직 JSON 형식으로만** 응답해줘. 다른 설명 문장은 붙이지 마.

                {
                  "introText": "완성된 소개글 (2~4문장)",
                  "recruitCount": 모집 인원 (숫자, 알 수 없으면 null),
                  "roles": ["모집 역할 목록"],
                  "periodEnd": "진행 기간 종료일 (yyyy-MM-dd, 알 수 없으면 null)",
                  "meetingSchedule": "회의 일정 (알 수 없으면 null)",
                  "deadline": "모집 마감일 (yyyy-MM-dd, 알 수 없으면 null)"
                }
                """.formatted(todayText, request.getTitle(), request.getRawIntroText());

        String rawResponse = (request.getFile() != null && !request.getFile().isEmpty())
                ? openAiClient.generateTextWithFile(prompt, request.getFile())
                : openAiClient.generateText(prompt);

        try {
            String json = openAiClient.extractJson(rawResponse);
            JsonNode node = objectMapper.readTree(json);

            List<String> roles = new java.util.ArrayList<>();
            if (node.has("roles") && node.get("roles").isArray()) {
                node.get("roles").forEach(r -> roles.add(r.asText()));
            }

            return ProjectAiGenerateResponse.builder()
                    .introText(node.path("introText").isNull() ? null : node.path("introText").asText())
                    .recruitCount(node.path("recruitCount").isNull() ? null : node.path("recruitCount").asInt())
                    .roles(roles)
                    .periodEnd(parseDateOrNull(node.path("periodEnd")))
                    .meetingSchedule(node.path("meetingSchedule").isNull() ? null : node.path("meetingSchedule").asText())
                    .deadline(parseDateOrNull(node.path("deadline")))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 파싱에 실패했습니다: " + e.getMessage(), e);
        }
    }

    private LocalDate parseDateOrNull(JsonNode node) {
        if (node.isNull() || node.asText().isBlank()) return null;
        try {
            return LocalDate.parse(node.asText());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 최종 확정된 프로젝트 정보를 저장한다.
     * 생성과 동시에: 작성자를 팀원으로 자동 등록, 초대 QR 생성, 채팅방 생성.
     */
    public ProjectCreateResponse createProject(ProjectCreateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. memberId=" + memberId));

        String rolesJoined = (request.getRoles() == null)
                ? null
                : String.join(",", request.getRoles());

        Project project = Project.builder()
                .title(request.getTitle())
                .introText(request.getIntroText())
                .recruitCount(request.getRecruitCount())
                .roles(rolesJoined)
                .periodEnd(request.getPeriodEnd())
                .meetingSchedule(request.getMeetingSchedule())
                .deadline(request.getDeadline())
                .imageUrl(request.getImageUrl())
                .member(member)
                .build();

        Project saved = projectRepository.save(project);

        // 프로젝트를 만든 사람을 팀원(ProjectMember)으로 자동 등록
        ProjectMember creatorMembership = ProjectMember.builder()
                .project(saved)
                .member(member)
                .role(null) // 역할은 AI 배정 전까지 미정
                .build();
        projectMemberRepository.save(creatorMembership);

        // 프로젝트 전용 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .project(saved)
                .build();
        chatRoomRepository.save(chatRoom);

        // 초대링크 생성 후, 그 링크를 QR 이미지로 실제 생성
        String inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String inviteLink = "fitple.app/invite/" + inviteCode;
        String qrCodeUrl = qrCodeService.generateQrCode(inviteLink);

        return ProjectCreateResponse.builder()
                .projectId(saved.getId())
                .inviteLink(inviteLink)
                .qrCodeUrl(qrCodeUrl)
                .build();
    }

    /**
     * 프로젝트 수정. 게시자 본인만 수정 가능하다.
     */
    public void updateProject(Long projectId, ProjectUpdateRequest request, Long memberId) {
        Project project = getProjectOrThrow(projectId);
        validateOwner(project, memberId);

        if (request.getTitle() != null) project.setTitle(request.getTitle());
        if (request.getIntroText() != null) project.setIntroText(request.getIntroText());
        if (request.getRecruitCount() != null) project.setRecruitCount(request.getRecruitCount());
        if (request.getRoles() != null) project.setRoles(String.join(",", request.getRoles()));
        if (request.getPeriodEnd() != null) project.setPeriodEnd(request.getPeriodEnd());
        if (request.getMeetingSchedule() != null) project.setMeetingSchedule(request.getMeetingSchedule());
        if (request.getDeadline() != null) project.setDeadline(request.getDeadline());
        if (request.getImageUrl() != null) project.setImageUrl(request.getImageUrl());

        projectRepository.save(project);
    }

    /**
     * 프로젝트 삭제. 게시자 본인만 삭제 가능하다.
     * 연관된 ProjectMember를 먼저 정리해야 FK 제약 위반이 나지 않는다.
     */
    public void deleteProject(Long projectId, Long memberId) {
        Project project = getProjectOrThrow(projectId);
        validateOwner(project, memberId);

        projectMemberRepository.deleteByProjectId(projectId);
        projectRepository.delete(project);
    }

    /**
     * 프로젝트 상세 조회.
     */
    public ProjectResponse getProject(Long projectId) {
        Project project = getProjectOrThrow(projectId);
        return ProjectResponse.from(project);
    }

    /**
     * 상태별 프로젝트 목록 조회. status가 null이면 전체 조회.
     */
    public List<ProjectSummaryResponse> getProjects(Project.ProjectStatus status) {
        List<Project> projects = (status == null)
                ? projectRepository.findAll()
                : projectRepository.findByStatus(status);

        return projects.stream()
                .map(ProjectSummaryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 개인 추천 프로젝트 목록.
     * TODO: 지금은 모집중인 프로젝트를 그대로 반환. 추후 추천 로직(관심분야/역량 매칭 등) 추가 필요.
     */
    public List<ProjectSummaryResponse> getRecommendedProjects(Long memberId) {
        return getProjects(Project.ProjectStatus.RECRUITING);
    }

    /**
     * 내가 진행중인 프로젝트 목록 (ProjectMember 기준).
     */
    public List<ProjectMyResponse> getMyProjects(Long memberId) {
        List<ProjectMember> memberships = projectMemberRepository.findByMemberId(memberId);
        return memberships.stream()
                .map(ProjectMyResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 프로젝트의 팀원 리스트.
     */
    public List<ProjectMemberResponse> getProjectMembers(Long projectId) {
        List<ProjectMember> memberships = projectMemberRepository.findByProjectId(projectId);
        return memberships.stream()
                .map(ProjectMemberResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 프로젝트 팀원들의 프로필을 분석해 AI(Gemini)가 역할을 배정한다.
     */
    public List<AssignRoleResponse> assignRoles(Long projectId) {
        Project project = getProjectOrThrow(projectId);
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);

        if (members.isEmpty()) {
            return List.of();
        }

        String rolesText = (project.getRoles() == null || project.getRoles().isBlank())
                ? "미정"
                : project.getRoles();

        String memberListText = members.stream()
                .map(pm -> "- memberId: " + pm.getMember().getId() + ", 이름: " + pm.getMember().getName())
                .collect(Collectors.joining("\n"));

        String prompt = """
                너는 대학생 프로젝트 팀 구성을 돕는 도우미야.
                아래 프로젝트에 모인 팀원들에게 역할을 배정해줘.

                프로젝트 제목: %s
                프로젝트 소개: %s
                모집 역할 목록: %s

                팀원 목록:
                %s

                각 팀원에게 위 모집 역할 목록 중 하나를 배정하고, 그렇게 배정한 이유를 한 문장으로 작성해줘.
                팀원 수가 역할 수보다 많으면 역할을 나눠 가질 수 있고, 특별한 근거가 없다면 골고루 배정해줘.
                **오직 JSON 배열 형식으로만** 응답해줘. 다른 설명 문장은 붙이지 마.

                [
                  { "memberId": 숫자, "role": "배정된 역할", "reason": "배정 이유 한 문장" }
                ]
                """.formatted(project.getTitle(), project.getIntroText(), rolesText, memberListText);

        String rawResponse = openAiClient.generateText(prompt);

        try {
            String json = openAiClient.extractJson(rawResponse);
            JsonNode arrayNode = objectMapper.readTree(json);

            List<AssignRoleResponse> result = new java.util.ArrayList<>();

            for (JsonNode item : arrayNode) {
                Long memberId = item.path("memberId").asLong();
                String role = item.path("role").asText();
                String reason = item.path("reason").asText();

                ProjectMember pm = members.stream()
                        .filter(m -> m.getMember().getId().equals(memberId))
                        .findFirst()
                        .orElse(null);

                if (pm == null) continue;

                pm.setRole(role);
                projectMemberRepository.save(pm);

                result.add(AssignRoleResponse.builder()
                        .memberId(memberId)
                        .name(pm.getMember().getName())
                        .role(role)
                        .reason(reason)
                        .build());
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("AI 역할 배정 응답 파싱에 실패했습니다: " + e.getMessage(), e);
        }
    }

    private Project getProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다. projectId=" + projectId));
    }

    private void validateOwner(Project project, Long memberId) {
        if (!project.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("본인이 작성한 프로젝트만 수정/삭제할 수 있습니다.");
        }
    }
}