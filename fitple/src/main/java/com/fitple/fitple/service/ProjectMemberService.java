package com.fitple.fitple.service;

import com.fitple.fitple.domain.ProjectMember;
import com.fitple.fitple.dto.response.ProjectMemberResponse;
import com.fitple.fitple.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.domain.Project;
import com.fitple.fitple.dto.request.ProjectMemberCreateRequest;
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.repository.ProjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    // 특정 프로젝트의 참여자 조회
    public List<ProjectMemberResponse> getProjectMembers(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId)
                .stream()
                .map(ProjectMemberResponse::from)
                .toList();
    }

    // 특정 회원이 참여한 프로젝트 조회
    public List<ProjectMemberResponse> getMemberProjects(Long memberId) {
        return projectMemberRepository.findByMemberId(memberId)
                .stream()
                .map(ProjectMemberResponse::from)
                .toList();
    }

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    public ProjectMemberResponse addProjectMember(
            Long projectId,
            ProjectMemberCreateRequest request
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (projectMemberRepository.existsByProjectIdAndMemberId(
                projectId,
                request.getMemberId()
        )) {
            throw new IllegalArgumentException("이미 프로젝트에 참여한 회원입니다.");
        }

        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .member(member)
                .build();

        ProjectMember savedProjectMember = projectMemberRepository.save(projectMember);

        return ProjectMemberResponse.from(savedProjectMember);
    }

}