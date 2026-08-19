package com.fitple.fitple.controller;

import com.fitple.fitple.dto.request.ApplicationAiGenerateRequest;
import com.fitple.fitple.dto.request.ApplicationCreateRequest;
import com.fitple.fitple.dto.response.ApplicationAiGenerateResponse;
import com.fitple.fitple.dto.response.ApplicationCreateResponse;
import com.fitple.fitple.dto.response.ApplicationResponse;
import com.fitple.fitple.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "지원 제출", description = "프로젝트에 지원합니다. 로그인이 필요합니다.")
    @PostMapping("/api/projects/{projectId}/applications")
    public ResponseEntity<ApplicationCreateResponse> createApplication(
            @PathVariable Long projectId,
            @RequestBody ApplicationCreateRequest request,
            HttpSession session
    ) {
        Long memberId = getMemberIdOrThrow(session);
        return ResponseEntity.ok(applicationService.createApplication(projectId, request, memberId));
    }

    @Operation(summary = "지원 목록 조회", description = "게시자 본인만 조회할 수 있습니다. 로그인이 필요합니다.")
    @GetMapping("/api/projects/{projectId}/applications")
    public ResponseEntity<List<ApplicationResponse>> getApplications(
            @PathVariable Long projectId,
            HttpSession session
    ) {
        Long memberId = getMemberIdOrThrow(session);
        return ResponseEntity.ok(applicationService.getApplications(projectId, memberId));
    }

    @Operation(summary = "지원 수락", description = "게시자 본인만 가능합니다. 수락 시 팀원(ProjectMember)으로 전환됩니다. 로그인이 필요합니다.")
    @PostMapping("/api/projects/{projectId}/applications/{applicationId}/accept")
    public ResponseEntity<Void> acceptApplication(
            @PathVariable Long projectId,
            @PathVariable Long applicationId,
            HttpSession session
    ) {
        Long memberId = getMemberIdOrThrow(session);
        applicationService.acceptApplication(projectId, applicationId, memberId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "지원 거절", description = "게시자 본인만 가능합니다. 로그인이 필요합니다.")
    @PostMapping("/api/projects/{projectId}/applications/{applicationId}/reject")
    public ResponseEntity<Void> rejectApplication(
            @PathVariable Long projectId,
            @PathVariable Long applicationId,
            HttpSession session
    ) {
        Long memberId = getMemberIdOrThrow(session);
        applicationService.rejectApplication(projectId, applicationId, memberId);
        return ResponseEntity.ok().build();
    }

    private Long getMemberIdOrThrow(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return memberId;
    }
}