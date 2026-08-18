package com.fitple.fitple.controller;

import com.fitple.fitple.dto.response.ChatProjectListResponse;
import com.fitple.fitple.dto.response.ChatRoomResponse;
import com.fitple.fitple.service.ChatService;
import com.fitple.fitple.service.ChatTranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fitple.fitple.dto.request.ChatMessageRequest;
import com.fitple.fitple.dto.response.ChatMessageResponse;
import jakarta.validation.Valid;

import com.fitple.fitple.dto.request.ChatTranslationRequest;
import com.fitple.fitple.dto.response.ChatTranslationResponse;
import com.fitple.fitple.service.ChatTranslationService;
import org.springframework.web.multipart.MultipartFile;

import com.fitple.fitple.dto.request.MeetingMinuteCreateRequest;
import com.fitple.fitple.dto.response.MeetingMinuteResponse;

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
    public ResponseEntity<Void> uploadFile(
            @PathVariable Long projectId,
            @RequestParam Long memberId,
            @RequestParam("file") MultipartFile file
    ) {
        chatService.uploadFile(
                projectId,
                memberId,
                file
        );

        return ResponseEntity.ok().build();
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
}