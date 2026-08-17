package com.fitple.fitple.controller;

import com.fitple.fitple.dto.response.ProjectResponse;
import com.fitple.fitple.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.fitple.fitple.dto.request.ProjectCreateRequest;
import jakarta.validation.Valid;

import com.fitple.fitple.dto.response.ProjectDetailResponse;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import com.fitple.fitple.dto.request.ProjectUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;


    // 프로젝트 전체 조회
    @GetMapping
    public List<ProjectResponse> getProjects() {
        return projectService.getProjects();
    }


    // 프로젝트 단건 조회
    @GetMapping("/{projectId}")
    public ProjectDetailResponse getProject(
            @PathVariable Long projectId
    ) {
        return projectService.getProject(projectId);
    }

    @PostMapping
    public ProjectResponse createProject(
            @Valid @RequestBody ProjectCreateRequest request,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return projectService.createProject(request, memberId);
    }
    @PutMapping("/{projectId}")
    public ProjectResponse updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequest request,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return projectService.updateProject(projectId, request);
    }
    @DeleteMapping("/{projectId}")
    public void deleteProject(
            @PathVariable Long projectId,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        projectService.deleteProject(projectId);
    }
}