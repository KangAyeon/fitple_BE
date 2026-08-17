package com.fitple.fitple.controller;

import com.fitple.fitple.dto.response.IntroductionListResponse;
import com.fitple.fitple.service.IntroductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/introductions")
@RequiredArgsConstructor
public class IntroductionController {

    private final IntroductionService introductionService;

    // 자기 역량 목록 조회
    @GetMapping
    public ResponseEntity<IntroductionListResponse> getIntroductions(
            @RequestParam Long memberId
    ) {
        return ResponseEntity.ok(
                introductionService.getIntroductions(memberId)
        );
    }

    // 자기 역량 상세 조회
    @GetMapping("/{introductionId}")
    public ResponseEntity<IntroductionListResponse.IntroductionResponse> getIntroduction(
            @RequestParam Long memberId,
            @PathVariable Long introductionId
    ) {
        return ResponseEntity.ok(
                introductionService.getIntroduction(
                        memberId,
                        introductionId
                )
        );
    }
}