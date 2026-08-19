package com.fitple.fitple.controller;

import com.fitple.fitple.domain.Project;
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
import com.fitple.fitple.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "AI 소개글 생성", description = "직접 작성한 소개글(+선택적 파일)을 참고해 AI가 최종 소개글과 구조화된 정보를 생성합니다.")
    @PostMapping(value = "/ai-generate", consumes = "multipart/form-data")
    public ResponseEntity<ProjectAiGenerateResponse> generateIntro(
            @ModelAttribute ProjectAiGenerateRequest request
    ) {
        return ResponseEntity.ok(projectService.generateIntro(request));
    }

    @Operation(summary = "프로젝트 생성", description = "최종 확정된 프로젝트 정보를 저장하고 초대 링크/QR코드를 생성합니다.")
    @PostMapping
    public ResponseEntity<ProjectCreateResponse> createProject(
            @Valid @RequestBody ProjectCreateRequest request,
            HttpSession session
    ) {
        Long memberId = getMemberIdOrThrow(session);
        return ResponseEntity.ok(projectService.createProject(request, memberId));
    }

    @Operation(summary = "프로젝트 수정", description = "게시자 본인만 수정할 수 있습니다. 보낸 필드만 수정되고, 나머지는 기존 값을 유지합니다.")
    @PutMapping("/{projectId}")
    public ResponseEntity<Void> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequest request,
            HttpSession session
    ) {
        Long memberId = getMemberIdOrThrow(session);
        projectService.updateProject(projectId, request, memberId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로젝트 삭제", description = "게시자 본인만 삭제할 수 있습니다.")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long projectId,
            HttpSession session
    ) {
        Long memberId = getMemberIdOrThrow(session);
        projectService.deleteProject(projectId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "프로젝트 상세 조회")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(projectService.getProject(projectId));
    }

    @Operation(summary = "프로젝트 목록 조회", description = "status를 넘기지 않으면 전체 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getProjects(
            @RequestParam(required = false) Project.ProjectStatus status
    ) {
        return ResponseEntity.ok(projectService.getProjects(status));
    }

    @Operation(summary = "개인 추천 프로젝트 목록")
    @GetMapping("/recommended")
    public ResponseEntity<List<ProjectSummaryResponse>> getRecommendedProjects(
            HttpSession session
    ) {
        Long memberId = getMemberIdOrThrow(session);
        return ResponseEntity.ok(projectService.getRecommendedProjects(memberId));
    }

    @Operation(summary = "내가 진행중인 프로젝트 목록")
    @GetMapping("/my")
    public ResponseEntity<List<ProjectMyResponse>> getMyProjects(
            HttpSession session
    ) {
        Long memberId = getMemberIdOrThrow(session);
        return ResponseEntity.ok(projectService.getMyProjects(memberId));
    }

    @Operation(summary = "프로젝트 팀원 리스트")
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberResponse>> getProjectMembers(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(projectService.getProjectMembers(projectId));
    }

    @Operation(summary = "AI 역할 배정", description = "프로젝트 팀원들의 프로필을 분석해 역할을 자동 배정합니다.")
    @PostMapping("/{projectId}/assign-roles")
    public ResponseEntity<List<AssignRoleResponse>> assignRoles(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(projectService.assignRoles(projectId));
    }

    @Operation(summary = "초대 링크로 프로젝트 조회", description = "QR코드/초대링크의 inviteCode로 프로젝트 정보를 조회합니다. 로그인 불필요.")
    @GetMapping("/invite/{inviteCode}")
    public ResponseEntity<ProjectResponse> getProjectByInviteCode(
            @PathVariable String inviteCode
    ) {
        return ResponseEntity.ok(projectService.getProjectByInviteCode(inviteCode));
    }

    private Long getMemberIdOrThrow(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return memberId;
    }
}