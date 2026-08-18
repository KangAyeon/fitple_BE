package com.fitple.fitple.service;

import com.fitple.fitple.domain.Application;
import com.fitple.fitple.domain.Member;
import com.fitple.fitple.domain.Project;
import com.fitple.fitple.dto.request.ApplicationAiGenerateRequest;
import com.fitple.fitple.dto.request.ApplicationCreateRequest;
import com.fitple.fitple.dto.response.ApplicationAiGenerateResponse;
import com.fitple.fitple.dto.response.ApplicationCreateResponse;
import com.fitple.fitple.repository.ApplicationRepository;
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

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
}