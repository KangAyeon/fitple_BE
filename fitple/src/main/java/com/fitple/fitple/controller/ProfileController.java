package com.fitple.fitple.controller;


import com.fitple.fitple.dto.request.ProfileGenerateRequest;
//import com.fitple.fitple.dto.request.ProfileRegenerateRequest;
import com.fitple.fitple.dto.request.ProfileUpdateRequest;
import com.fitple.fitple.dto.response.MessageResponse;
import com.fitple.fitple.dto.response.ProfileDetailResponse;
import com.fitple.fitple.dto.response.ProfileGenerateResponse;
import com.fitple.fitple.dto.response.ProfileResponse;
import com.fitple.fitple.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ProfileResponse> generateProfile(
            @RequestBody ProfileGenerateRequest request,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(
                profileService.generateProfile(
                        memberId,
                        request
                )
        );
    }

    @PostMapping("/regenerate")
    public ResponseEntity<ProfileResponse> regenerateProfile(
            @RequestBody ProfileGenerateRequest request
    ) {
        return ResponseEntity.ok(
                profileService.regenerateProfile(request)
        );
    }

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
}