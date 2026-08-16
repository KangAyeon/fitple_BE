package com.fitple.fitple.service;

import com.fitple.fitple.domain.Project;
import com.fitple.fitple.dto.response.ProjectResponse;
import com.fitple.fitple.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.fitple.fitple.dto.request.ProjectCreateRequest;
import java.time.LocalDateTime;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.domain.ProjectMember;
import com.fitple.fitple.repository.ProjectMemberRepository;

import com.fitple.fitple.dto.response.ProjectDetailResponse;
import com.fitple.fitple.dto.response.ProjectMemberResponse;

import com.fitple.fitple.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    // 프로젝트 전체 조회
    public List<ProjectResponse> getProjects() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    // 프로젝트 단건 조회
    public ProjectDetailResponse getProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        List<ProjectMemberResponse> members = projectMemberRepository.findByProjectId(projectId)
                .stream()
                .map(ProjectMemberResponse::from)
                .toList();

        return ProjectDetailResponse.from(project, members);
    }

    public ProjectResponse createProject(ProjectCreateRequest request, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Project project = Project.builder()
                .name(request.getName())
                .iconUrl(request.getIconUrl())
                .recruiting(true)
                .createdAt(LocalDateTime.now())
                .build();

        Project savedProject = projectRepository.save(project);

        ProjectMember projectMember = ProjectMember.builder()
                .project(savedProject)
                .member(member)
                .build();

        projectMemberRepository.save(projectMember);

        return ProjectResponse.from(savedProject);
    }
}