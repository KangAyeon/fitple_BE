package com.fitple.fitple.controller;

import com.fitple.fitple.dto.request.ProfileGenerateRequest;
import com.fitple.fitple.dto.request.ProfileUpdateRequest;
import com.fitple.fitple.dto.response.MessageResponse;
import com.fitple.fitple.dto.response.ProfileDetailResponse;
import com.fitple.fitple.dto.response.ProfileFileResponse;
import com.fitple.fitple.dto.response.ProfileResponse;
import com.fitple.fitple.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(
            summary = "프로필 생성",
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 생성 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @PostMapping("/generate")
    public ResponseEntity<ProfileResponse> generateProfile(
            @RequestBody ProfileGenerateRequest request
    ) {
        return ResponseEntity.ok(
                profileService.generateProfile(request)
        );
    }

    @Operation(
            summary = "프로필 재생성",
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 재생성 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @PostMapping("/regenerate")
    public ResponseEntity<ProfileResponse> regenerateProfile(
            @RequestBody ProfileGenerateRequest request
    ) {
        return ResponseEntity.ok(
                profileService.regenerateProfile(request)
        );
    }

    @Operation(
            summary = "프로필 조회",
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @GetMapping
    public ResponseEntity<ProfileDetailResponse> getProfile(
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(
                profileService.getProfile(memberId)
        );
    }

    @Operation(
            summary = "프로필 수정",
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @PutMapping
    public ResponseEntity<MessageResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            HttpSession session
    ) {
        Long currentMemberId =
                (Long) session.getAttribute("memberId");

        if (currentMemberId == null) {
            return ResponseEntity.status(401).build();
        }

        MessageResponse response =
                profileService.updateProfile(
                        currentMemberId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "프로필 파일 업로드",
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "파일 업로드 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @PostMapping(
            value = "/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProfileFileResponse> uploadProfileFile(
            @RequestParam Long memberId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(
                profileService.uploadProfileFile(memberId, file)
        );
    }
}