package com.fitple.fitple.controller;

import com.fitple.fitple.service.ProjectScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class ProjectScrapController {

    private final ProjectScrapService projectScrapService;

    @PostMapping("/scraps")
    public ResponseEntity<Void> addScrap(
            @RequestParam Long memberId,
            @RequestParam Long projectId
    ) {
        projectScrapService.addScrap(memberId, projectId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/scraps")
    public ResponseEntity<Void> removeScrap(
            @RequestParam Long memberId,
            @RequestParam Long projectId
    ) {
        projectScrapService.removeScrap(memberId, projectId);
        return ResponseEntity.ok().build();
    }
}