package com.fitple.fitple.controller;

import com.fitple.fitple.dto.request.ApplicationAiGenerateRequest;
import com.fitple.fitple.dto.request.ApplicationCreateRequest;
import com.fitple.fitple.dto.response.ApplicationAiGenerateResponse;
import com.fitple.fitple.dto.response.ApplicationCreateResponse;
import com.fitple.fitple.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "지원용 소개글 AI 생성", description = "직접 작성한 소개글을 AI가 다듬어줍니다. 파일 첨부는 없습니다.")
    @PostMapping("/api/applications/ai-generate")
    public ResponseEntity<ApplicationAiGenerateResponse> generateIntro(
            @RequestBody ApplicationAiGenerateRequest request
    ) {
        return ResponseEntity.ok(applicationService.generateIntro(request));
    }

    @Operation(summary = "지원 제출", description = "프로젝트에 지원합니다.")
    @PostMapping("/api/projects/{projectId}/applications")
    public ResponseEntity<ApplicationCreateResponse> createApplication(
            @PathVariable Long projectId,
            @RequestBody ApplicationCreateRequest request,
            @Parameter(description = "지원자(로그인 회원) ID") @RequestParam Long memberId
    ) {
        return ResponseEntity.ok(applicationService.createApplication(projectId, request, memberId));
    }
}