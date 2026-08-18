package com.fitple.fitple.controller;

import com.fitple.fitple.dto.request.MyPageUpdateRequest;
import com.fitple.fitple.dto.response.MyPageResponse;
import com.fitple.fitple.dto.response.ScrapListResponse;
import com.fitple.fitple.service.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fitple.fitple.service.ProjectScrapService;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final ProjectScrapService projectScrapService;
    private final MyPageService myPageService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MyPageResponse> getMyPage(
            @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(
                myPageService.getMyPage(memberId)
        );
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<MyPageResponse> updateMyPage(
            @PathVariable Long memberId,
            @Valid @RequestBody MyPageUpdateRequest request
    ) {
        return ResponseEntity.ok(
                myPageService.updateMyPage(memberId, request)
        );
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMyPage(
            @PathVariable Long memberId
    ) {
        myPageService.deleteMyPage(memberId);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/scraps")
    public ScrapListResponse getScraps(
            @RequestParam Long memberId
    ) {
        return projectScrapService.getScraps(memberId);
    }
}