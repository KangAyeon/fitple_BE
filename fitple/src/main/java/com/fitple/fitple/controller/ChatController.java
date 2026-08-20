package com.fitple.fitple.controller;

import com.fitple.fitple.dto.response.*;
import com.fitple.fitple.service.ChatService;
import com.fitple.fitple.service.ChatTranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fitple.fitple.dto.request.ChatMessageRequest;
import jakarta.validation.Valid;

import com.fitple.fitple.dto.request.ChatTranslationRequest;
import com.fitple.fitple.service.ChatTranslationService;
import org.springframework.web.multipart.MultipartFile;

import com.fitple.fitple.dto.request.MeetingMinuteCreateRequest;
import com.fitple.fitple.dto.response.ChatFileResponse;

import com.fitple.fitple.dto.request.ScheduleUpdateRequests;
import com.fitple.fitple.dto.response.ScheduleUpdateResponse;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    private final ChatTranslationService chatTranslationService;

    // 채팅 프로젝트 목록 조회
    @GetMapping("/projects")
    public ResponseEntity<ChatProjectListResponse> getChatProjects(
            @RequestParam Long memberId
    ) {
        return ResponseEntity.ok(
                chatService.getChatProjects(memberId)
        );
    }
    @GetMapping("/rooms/{projectId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                chatService.getChatRoom(projectId)
        );
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long roomId,
            @RequestParam Long memberId,
            @Valid @RequestBody ChatMessageRequest request
    ) {
        return ResponseEntity.ok(
                chatService.sendMessage(
                        roomId,
                        memberId,
                        request
                )
        );
    }
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Integer size
    ) {
        if (size != null) {
            return ResponseEntity.ok(
                    chatService.getPreviousMessages(roomId, size)
            );
        }

        return ResponseEntity.ok(
                chatService.getMessages(roomId)
        );
    }

    @PostMapping("/translate")
    public ResponseEntity<ChatTranslationResponse> translate(
            @Valid @RequestBody ChatTranslationRequest request
    ) {
        return ResponseEntity.ok(
                chatTranslationService.translate(request)
        );
    }

    @PostMapping("/rooms/{projectId}")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                chatService.createChatRoom(projectId)
        );
    }
    @GetMapping("/rooms/{roomId}/messages/previous")
    public ResponseEntity<List<ChatMessageResponse>> getPreviousMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                chatService.getPreviousMessages(roomId, size)
        );
    }

    @PostMapping(
            value = "/rooms/{projectId}/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ChatFileResponse> uploadFile(
            @PathVariable Long projectId,
            @RequestParam Long memberId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(
                chatService.uploadFile(
                        projectId,
                        memberId,
                        file
                )
        );
    }


    @PostMapping("/rooms/{projectId}/meeting-minutes")
    public ResponseEntity<MeetingMinuteResponse> createMeetingMinute(
            @PathVariable Long projectId,
            @Valid @RequestBody MeetingMinuteCreateRequest request
    ) {
        return ResponseEntity.ok(
                chatService.createMeetingMinute(
                        projectId,
                        request
                )
        );
    }

    @GetMapping("/rooms/{projectId}/meeting-minutes")
    public ResponseEntity<List<MeetingMinuteResponse>> getMeetingMinutes(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                chatService.getMeetingMinutes(projectId)
        );
    }

    @PostMapping("/rooms/{roomId}/meeting-minutes/ai-generate")
    public ResponseEntity<MeetingMinuteResponse> generateMeetingMinuteByAI(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(
                chatService.generateMeetingMinuteByAI(roomId)
        );
    }

    @GetMapping("/rooms/{projectId}/meeting-minutes/{meetingMinuteId}")
    public ResponseEntity<MeetingMinuteResponse> getMeetingMinute(
            @PathVariable Long projectId,
            @PathVariable Long meetingMinuteId
    ) {
        return ResponseEntity.ok(
                chatService.getMeetingMinute(
                        projectId,
                        meetingMinuteId
                )
        );
    }
    @PostMapping("/rooms/{roomId}/tasks/ai-generate")
    public ResponseEntity<List<TaskResponse>> generateTodayTasks(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(
                chatService.generateTodayTasks(roomId)
        );
    }
    @GetMapping("/rooms/{projectId}/members")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                chatService.getTeamMembers(projectId)
        );
    }
    @GetMapping("/rooms/{roomId}/files")
    public ResponseEntity<List<ChatFileResponse>> getChatFiles(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(
                chatService.getChatFiles(roomId)
        );
    }

    @PostMapping("/rooms/{roomId}/roadmap/ai-generate")
    public ResponseEntity<List<RoadmapStageResponse>> generateRoadmap(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(
                chatService.generateRoadmap(roomId)
        );
    }

    @GetMapping("/rooms/{projectId}/roadmap")
    public ResponseEntity<RoadmapResponse> getRoadmap(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                chatService.getRoadmap(projectId)
        );
    }

    @PostMapping("/rooms/{roomId}/schedule/ai-update")
    public ResponseEntity<List<ScheduleUpdateResponse>> updateSchedule(
            @PathVariable Long roomId,
            @RequestBody ScheduleUpdateRequests request
    ) {

        return ResponseEntity.ok(
                chatService.updateSchedule(
                        roomId,
                        request.getUpdates()
                )
        );
    }
}