package com.fitple.fitple.service;

import com.fitple.fitple.domain.Application;
import com.fitple.fitple.domain.Member;
import com.fitple.fitple.domain.Project;
import com.fitple.fitple.domain.ProjectMember;
import com.fitple.fitple.dto.request.ApplicationAiGenerateRequest;
import com.fitple.fitple.dto.request.ApplicationCreateRequest;
import com.fitple.fitple.dto.response.ApplicationAiGenerateResponse;
import com.fitple.fitple.dto.response.ApplicationCreateResponse;
import com.fitple.fitple.dto.response.ApplicationMyResponse;
import com.fitple.fitple.dto.response.ApplicationResponse;
import com.fitple.fitple.exception.DuplicateApplicationException;
import com.fitple.fitple.repository.ApplicationRepository;
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.repository.ProjectMemberRepository;
import com.fitple.fitple.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final OpenAiClient openAiClient;

    /**
     * 지원용 소개글을 AI(Gemini)가 다듬어준다. (텍스트만 입력, 파일 없음)
     */
    public ApplicationAiGenerateResponse generateIntro(ApplicationAiGenerateRequest request) {
        String prompt = """
                너는 대학생 프로젝트 지원서의 자기소개글을 다듬어주는 도우미야.
                아래는 지원자가 직접 작성한 소개글 초안이야.

                초안: %s

                이 내용을 자연스럽고 설득력 있게 다듬어서, 다듬어진 소개글 텍스트만 응답해줘.
                따옴표나 설명, 다른 문구 없이 순수 소개글 본문만 반환해.
                """.formatted(request.getRawIntroText());

        String response = openAiClient.generateText(prompt);

        return ApplicationAiGenerateResponse.builder()
                .introText(response.trim())
                .build();
    }

    /**
     * 지원 제출. 같은 회원이 같은 프로젝트에 PENDING 또는 ACCEPTED 상태로 이미 지원했다면 409로 거절한다.
     */
    @Transactional
    public ApplicationCreateResponse createApplication(Long projectId, ApplicationCreateRequest request, Long memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다. projectId=" + projectId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. memberId=" + memberId));

        boolean alreadyApplied = applicationRepository.existsByProjectIdAndMemberIdAndStatusIn(
                projectId,
                memberId,
                List.of(Application.ApplicationStatus.PENDING, Application.ApplicationStatus.ACCEPTED)
        );

        if (alreadyApplied) {
            throw new DuplicateApplicationException("이미 지원한 프로젝트입니다.");
        }

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
     * 내가 지원한 목록 전체 조회 (지원 현황 화면용).
     */
    public List<ApplicationMyResponse> getMyApplications(Long memberId) {
        return applicationRepository.findByMemberId(memberId).stream()
                .map(ApplicationMyResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 지원 수락. 게시자 본인만 가능하며, 수락 시 ProjectMember로 전환된다.
     */
    @Transactional
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
    @Transactional
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