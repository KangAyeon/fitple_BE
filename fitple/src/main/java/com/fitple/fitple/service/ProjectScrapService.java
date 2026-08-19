package com.fitple.fitple.service;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.domain.Project;
import com.fitple.fitple.domain.ProjectScrap;
import com.fitple.fitple.dto.response.ScrapListResponse;
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.repository.ProjectRepository;
import com.fitple.fitple.repository.ProjectScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fitple.fitple.repository.ProjectRecruitRoleRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ProjectScrapService {

    private final ProjectScrapRepository projectScrapRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectRecruitRoleRepository projectRecruitRoleRepository;

    @Transactional
    public void addScrap(Long memberId, Long projectId) {

        if (projectScrapRepository.existsByMemberIdAndProjectId(memberId, projectId)) {
            throw new IllegalStateException("이미 스크랩한 프로젝트입니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        ProjectScrap scrap = ProjectScrap.builder()
                .member(member)
                .project(project)
                .build();

        projectScrapRepository.save(scrap);
    }

    @Transactional
    public void removeScrap(Long memberId, Long projectId) {

        if (!projectScrapRepository.existsByMemberIdAndProjectId(memberId, projectId)) {
            throw new IllegalArgumentException("스크랩하지 않은 프로젝트");
        }

        projectScrapRepository.deleteByMemberIdAndProjectId(
                memberId,
                projectId
        );
    }

    @Transactional(readOnly = true)
    public ScrapListResponse getScraps(Long memberId) {

        return ScrapListResponse.builder()
                .projects(
                        projectScrapRepository.findByMemberId(memberId)
                                .stream()
                                .map(scrap -> {

                                    var project = scrap.getProject();

                                    var recruitRoles =
                                            projectRecruitRoleRepository
                                                    .findByProjectId(project.getId())
                                                    .stream()
                                                    .map(role -> role.getRole())
                                                    .toList();

                                    return ScrapListResponse.ProjectResponse.builder()
                                            .projectId(project.getId())
                                            .projectIconUrl(project.getImageUrl())
                                            .title(project.getTitle())
                                            .recruitRoles(recruitRoles)
                                            .dDay(calculateDDay(project.getDeadline()))
                                            .recruitStatus(
                                                    project.getStatus() == Project.ProjectStatus.RECRUITING
                                                            ? "모집중"
                                                            : "모집마감"
                                            )
                                            .build();
                                })
                                .toList()
                )
                .build();
    }
    @Transactional
    public void toggleScrap(Long memberId, Long projectId) {

        if (projectScrapRepository.existsByMemberIdAndProjectId(memberId, projectId)) {
            projectScrapRepository.deleteByMemberIdAndProjectId(memberId, projectId);
        } else {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

            projectScrapRepository.save(
                    ProjectScrap.builder()
                            .member(member)
                            .project(project)
                            .build()
            );
        }
    }
    private String calculateDDay(LocalDate deadline) {

        if (deadline == null) {
            return null;
        }

        long days = ChronoUnit.DAYS.between(
                LocalDate.now(),
                deadline
        );

        if (days == 0) {
            return "D-DAY";
        }

        if (days > 0) {
            return "D-" + days;
        }

        return "D+" + Math.abs(days);
    }
}