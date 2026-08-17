package com.fitple.fitple.controller;

import com.fitple.fitple.dto.request.SettingsUpdateRequest;
import com.fitple.fitple.dto.response.SettingsResponse;
import com.fitple.fitple.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingController {

    private final UserSettingService userSettingService;

    // 환경 설정 조회
    @GetMapping
    public ResponseEntity<SettingsResponse> getSettings(
            @RequestParam Long memberId
    ) {
        return ResponseEntity.ok(
                userSettingService.getSettings(memberId)
        );
    }

    // 환경 설정 변경
    @PutMapping
    public ResponseEntity<SettingsResponse> updateSettings(
            @RequestParam Long memberId,
            @RequestBody SettingsUpdateRequest request
    ) {
        return ResponseEntity.ok(
                userSettingService.updateSettings(memberId, request)
        );
    }
}