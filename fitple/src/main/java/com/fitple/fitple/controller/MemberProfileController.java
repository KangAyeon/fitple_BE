package com.fitple.fitple.controller;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.dto.response.MemberProfileResponse;
import com.fitple.fitple.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 이미 존재하는 auth-controller(MemberController, /api/auth)와 클래스명이 겹쳐서
// MemberProfileController로 명명함
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberRepository memberRepository;

    @Operation(summary = "내 프로필 조회", description = "지원하기 화면 등에서 이름을 자동으로 불러올 때 사용합니다.")
    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> getMyProfile(
            @Parameter(description = "로그인 회원 ID") @RequestParam Long memberId
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. memberId=" + memberId));

        MemberProfileResponse response = MemberProfileResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .build();

        return ResponseEntity.ok(response);
    }
}