package com.fitple.fitple.service;

import com.fitple.fitple.domain.Introduction;
import com.fitple.fitple.dto.response.IntroductionListResponse;
import com.fitple.fitple.repository.IntroductionRepository;
import com.fitple.fitple.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IntroductionService {

    private final IntroductionRepository introductionRepository;
    private final MemberRepository memberRepository;

    // 자기 역량 목록 조회
    @Transactional(readOnly = true)
    public IntroductionListResponse getIntroductions(Long memberId) {

        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        return IntroductionListResponse.builder()
                .introductions(
                        introductionRepository.findByMemberId(memberId)
                                .stream()
                                .map(introduction ->
                                        IntroductionListResponse.IntroductionResponse
                                                .builder()
                                                .introductionId(introduction.getId())
                                                .title(introduction.getTitle())
                                                .content(introduction.getContent())
                                                .build()
                                )
                                .toList()
                )
                .build();
    }

    // 자기 역량 상세 조회
    @Transactional(readOnly = true)
    public IntroductionListResponse.IntroductionResponse getIntroduction(
            Long memberId,
            Long introductionId
    ) {

        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        Introduction introduction =
                introductionRepository
                        .findByIdAndMemberId(introductionId, memberId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "나의 역량 항목이 없습니다."
                                ));

        return IntroductionListResponse.IntroductionResponse.builder()
                .introductionId(introduction.getId())
                .title(introduction.getTitle())
                .content(introduction.getContent())
                .build();
    }
}