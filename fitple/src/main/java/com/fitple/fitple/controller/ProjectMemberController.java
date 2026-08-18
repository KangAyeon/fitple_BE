package com.fitple.fitple.controller;

import com.fitple.fitple.dto.response.ProjectMemberResponse;
import com.fitple.fitple.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.fitple.fitple.dto.request.ProjectMemberCreateRequest;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/project-members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    // 프로젝트 참여자 조회
    @GetMapping("/project/{projectId}")
    public List<ProjectMemberResponse> getProjectMembers(
            @PathVariable Long projectId
    ) {
        return projectMemberService.getProjectMembers(projectId);
    }

    // 회원이 참여한 프로젝트 조회
    @GetMapping("/member/{memberId}")
    public List<ProjectMemberResponse> getMemberProjects(
            @PathVariable Long memberId
    ) {
        return projectMemberService.getMemberProjects(memberId);
    }
    @PostMapping("/project/{projectId}")
    public ProjectMemberResponse addProjectMember(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectMemberCreateRequest request
    ) {
        return projectMemberService.addProjectMember(projectId, request);
    }
}