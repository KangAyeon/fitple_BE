package com.fitple.fitple.service;

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
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.repository.ProjectMemberRepository;
import com.fitple.fitple.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectMemberRepository projectMemberRepository;

    /**
     * 사용자가 직접 쓴 소개글(+파일이 있으면 파일)을 참고해서
     * AI가 최종 소개글과 구조화된 정보를 생성한다.
     *
     * TODO: 실제 AI API(예: Claude API) 연동 필요.
     *  - request.getRawIntroText() : 사용자가 직접 쓴 원본 소개글 (필수)
     *  - request.getFile()         : 참고 파일 (선택, null일 수 있음)
     *  - 파일이 있으면 파일 내용까지 함께 프롬프트에 포함해서 호출
     *  - AI 응답은 JSON 구조로 받아서 아래 필드에 매핑
     */
    public ProjectAiGenerateResponse generateIntro(ProjectAiGenerateRequest request) {
        // TODO: AI 호출 로직으로 교체
        // 아래는 임시 더미 응답 (프론트/연동 테스트용)
        return ProjectAiGenerateResponse.builder()
                .introText(request.getRawIntroText()) // TODO: AI가 다듬은 텍스트로 교체
                .recruitCount(null)
                .roles(List.of())
                .periodEnd(null)
                .meetingSchedule(null)
                .deadline(null)
                .build();
    }

    /**
     * 최종 확정된 프로젝트 정보를 저장한다.
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

        // TODO: 실제 QR/초대링크 생성 로직으로 교체 (별도 InviteCodeGenerator 등)
        String inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String inviteLink = "fitple.app/invite/" + inviteCode;
        String qrCodeUrl = "https://fitple.app/qr/" + inviteCode + ".png";

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
     */
    public void deleteProject(Long projectId, Long memberId) {
        Project project = getProjectOrThrow(projectId);
        validateOwner(project, memberId);
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
     * 프로젝트 팀원들의 프로필을 분석해 AI가 역할을 배정한다.
     * TODO: 실제 AI API 연동 필요. 지금은 팀원 순서대로 프로젝트의 roles를 나눠 배정하는 더미 로직.
     */
    public List<AssignRoleResponse> assignRoles(Long projectId) {
        Project project = getProjectOrThrow(projectId);
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);

        List<String> roles = (project.getRoles() == null || project.getRoles().isBlank())
                ? List.of()
                : Arrays.asList(project.getRoles().split(","));

        List<AssignRoleResponse> result = new java.util.ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            ProjectMember pm = members.get(i);

            // TODO: 실제로는 각 멤버의 프로필/역량 정보를 AI에 넘겨서 배정받아야 함
            String assignedRole = roles.isEmpty()
                    ? "미정"
                    : roles.get(i % roles.size());

            pm.setRole(assignedRole);
            projectMemberRepository.save(pm);

            result.add(AssignRoleResponse.builder()
                    .memberId(pm.getMember().getId())
                    .name(pm.getMember().getName())
                    .role(assignedRole)
                    .reason("TODO: AI 분석 결과로 교체 예정 (지금은 순번 배정)")
                    .build());
        }

        return result;
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