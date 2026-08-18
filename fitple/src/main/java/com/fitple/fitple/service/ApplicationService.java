package com.fitple.fitple.service;

import com.fitple.fitple.domain.Application;
import com.fitple.fitple.domain.Member;
import com.fitple.fitple.domain.Project;
import com.fitple.fitple.domain.ProjectMember;
import com.fitple.fitple.dto.request.ApplicationAiGenerateRequest;
import com.fitple.fitple.dto.request.ApplicationCreateRequest;
import com.fitple.fitple.dto.response.ApplicationAiGenerateResponse;
import com.fitple.fitple.dto.response.ApplicationCreateResponse;
import com.fitple.fitple.dto.response.ApplicationResponse;
import com.fitple.fitple.repository.ApplicationRepository;
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.repository.ProjectMemberRepository;
import com.fitple.fitple.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectMemberRepository projectMemberRepository;

    /**
     * 지원용 소개글을 AI가 다듬어준다. (텍스트만 입력, 파일 없음)
     * TODO: 실제 AI API 연동 필요. 지금은 원본을 그대로 돌려주는 더미 상태.
     */
    public ApplicationAiGenerateResponse generateIntro(ApplicationAiGenerateRequest request) {
        // TODO: AI 호출 로직으로 교체
        return ApplicationAiGenerateResponse.builder()
                .introText(request.getRawIntroText())
                .build();
    }

    /**
     * 지원 제출.
     */
    public ApplicationCreateResponse createApplication(Long projectId, ApplicationCreateRequest request, Long memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다. projectId=" + projectId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. memberId=" + memberId));

        Application application = Application.builder()
                .project(project)
                .member(member)
                .introText(request.getIntroText())
                .build();

        Application saved = applicationRepository.save(application);

        return ApplicationCreateResponse.builder()
                .applicationId(saved.getId())
                .status(saved.getStatus().name())
                .build();
    }

    /**
     * 특정 프로젝트에 대한 지원 목록 조회. 게시자 본인만 조회 가능하다.
     */
    public List<ApplicationResponse> getApplications(Long projectId, Long requesterId) {
        Project project = getProjectOrThrow(projectId);
        validateOwner(project, requesterId);

        return applicationRepository.findByProjectId(projectId).stream()
                .map(ApplicationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 지원 수락. 게시자 본인만 가능하며, 수락 시 ProjectMember로 전환된다.
     */
    public void acceptApplication(Long projectId, Long applicationId, Long requesterId) {
        Project project = getProjectOrThrow(projectId);
        validateOwner(project, requesterId);

        Application application = getApplicationOrThrow(applicationId);

        application.setStatus(Application.ApplicationStatus.ACCEPTED);
        applicationRepository.save(application);

        // 이미 팀원으로 등록되어 있지 않은 경우에만 추가 (중복 방지)
        boolean alreadyMember = projectMemberRepository.findByProjectId(projectId).stream()
                .anyMatch(pm -> pm.getMember().getId().equals(application.getMember().getId()));

        if (!alreadyMember) {
            ProjectMember newMember = ProjectMember.builder()
                    .project(project)
                    .member(application.getMember())
                    .role(null) // 역할은 AI 배정 전까지 미정
                    .build();
            projectMemberRepository.save(newMember);
        }
    }

    /**
     * 지원 거절. 게시자 본인만 가능하다.
     */
    public void rejectApplication(Long projectId, Long applicationId, Long requesterId) {
        Project project = getProjectOrThrow(projectId);
        validateOwner(project, requesterId);

        Application application = getApplicationOrThrow(applicationId);
        application.setStatus(Application.ApplicationStatus.REJECTED);
        applicationRepository.save(application);
    }

    private Project getProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다. projectId=" + projectId));
    }

    private Application getApplicationOrThrow(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지원 내역입니다. applicationId=" + applicationId));
    }

    private void validateOwner(Project project, Long requesterId) {
        if (!project.getMember().getId().equals(requesterId)) {
            throw new IllegalStateException("게시자 본인만 지원 내역을 확인/처리할 수 있습니다.");
        }
    }
}