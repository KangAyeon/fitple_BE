package com.fitple.fitple.controller;

import com.fitple.fitple.dto.response.ChatProjectListResponse;
import com.fitple.fitple.dto.response.ChatRoomResponse;
import com.fitple.fitple.service.ChatService;
import com.fitple.fitple.service.ChatTranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fitple.fitple.dto.request.ChatMessageRequest;
import com.fitple.fitple.dto.response.ChatMessageResponse;
import jakarta.validation.Valid;

import com.fitple.fitple.dto.request.ChatTranslationRequest;
import com.fitple.fitple.dto.response.ChatTranslationResponse;
import com.fitple.fitple.service.ChatTranslationService;

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
            @PathVariable Long roomId
    ) {
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
}