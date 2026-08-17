package com.fitple.fitple.service;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.domain.ProjectScrap;
import com.fitple.fitple.dto.request.MyPageUpdateRequest;
import com.fitple.fitple.dto.response.MyPageResponse;
import com.fitple.fitple.dto.response.ScrapListResponse;
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.repository.ProjectMemberRepository;
import com.fitple.fitple.repository.ProjectRecruitRoleRepository;
import com.fitple.fitple.repository.ProjectScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final ProjectMemberRepository projectMemberRepository;
    private final MemberRepository memberRepository;
    private final ProjectScrapRepository projectScrapRepository;
    private final ProjectRecruitRoleRepository projectRecruitRoleRepository;

    public MyPageResponse getMyPage(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다."));

        return MyPageResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .loginId(member.getLoginId())
                .build();
    }

    @Transactional
    public MyPageResponse updateMyPage(
            Long memberId,
            MyPageUpdateRequest request
    ) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.setName(request.getName());

        return MyPageResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .loginId(member.getLoginId())
                .build();
    }

    @Transactional
    public void deleteMyPage(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다."));

        projectMemberRepository.deleteAll(
                projectMemberRepository.findByMemberId(memberId)
        );

        memberRepository.delete(member);
    }

    // 스크랩 목록 조회
    public ScrapListResponse getScraps(Long memberId) {

        // 회원 존재 여부 확인
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        List<ProjectScrap> scraps =
                projectScrapRepository.findByMemberId(memberId);

        List<ScrapListResponse.ProjectResponse> projects =
                scraps.stream()
                        .map(scrap -> {

                            var project = scrap.getProject();

                            List<String> recruitRoles =
                                    projectRecruitRoleRepository
                                            .findByProjectId(project.getId())
                                            .stream()
                                            .map(recruitRole ->
                                                    recruitRole.getRole())
                                            .toList();

                            String dDay = calculateDDay(
                                    project.getRecruitDeadline()
                            );

                            String recruitStatus =
                                    project.isRecruiting()
                                            ? "모집중"
                                            : "모집마감";

                            return ScrapListResponse.ProjectResponse.builder()
                                    .projectId(project.getId())
                                    .projectIconUrl(project.getIconUrl())
                                    .title(project.getName())
                                    .recruitRoles(recruitRoles)
                                    .dDay(dDay)
                                    .recruitStatus(recruitStatus)
                                    .build();
                        })
                        .toList();

        return ScrapListResponse.builder()
                .projects(projects)
                .build();
    }

    private String calculateDDay(
            java.time.LocalDateTime recruitDeadline
    ) {

        if (recruitDeadline == null) {
            return "D-Day 미정";
        }

        LocalDate today = LocalDate.now();
        LocalDate deadline = recruitDeadline.toLocalDate();

        long days = ChronoUnit.DAYS.between(today, deadline);

        if (days > 0) {
            return "D-" + days;
        }

        if (days == 0) {
            return "D-Day";
        }

        return "모집마감";
    }
}