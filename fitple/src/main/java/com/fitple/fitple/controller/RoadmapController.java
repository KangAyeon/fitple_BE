package com.fitple.fitple.controller;

import com.fitple.fitple.dto.response.RoadmapStageResponse;
import com.fitple.fitple.service.AIRoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class RoadmapController {

    private final AIRoadmapService aiRoadmapService;

    @PostMapping("/{roomId}/roadmap/stages/ai-generate")
    public ResponseEntity<List<RoadmapStageResponse>> generateRoadmap(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(
                aiRoadmapService.generateRoadmap(roomId)
        );
    }
}