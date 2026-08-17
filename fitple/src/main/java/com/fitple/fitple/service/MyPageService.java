package com.fitple.fitple.service;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.dto.request.MyPageUpdateRequest;
import com.fitple.fitple.dto.response.MyPageResponse;
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final ProjectMemberRepository projectMemberRepository;
    private final MemberRepository memberRepository;

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
}