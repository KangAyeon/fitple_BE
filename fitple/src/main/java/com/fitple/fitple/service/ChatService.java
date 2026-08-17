package com.fitple.fitple.service;

import com.fitple.fitple.domain.Project;
import com.fitple.fitple.dto.response.ChatProjectListResponse;
import com.fitple.fitple.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitple.fitple.domain.ChatRoom;
import com.fitple.fitple.dto.response.ChatRoomResponse;

import com.fitple.fitple.domain.ChatMessage;
import com.fitple.fitple.domain.ChatRoom;
import com.fitple.fitple.domain.Member;
import com.fitple.fitple.dto.request.ChatMessageRequest;
import com.fitple.fitple.dto.response.ChatMessageResponse;
import com.fitple.fitple.repository.ChatRoomRepository;
import com.fitple.fitple.domain.Project;
import com.fitple.fitple.repository.ProjectRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ProjectMemberRepository projectMemberRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    private final ProjectRepository projectRepository;

    // 채팅 가능한 프로젝트 목록 조회
    @Transactional(readOnly = true)
    public ChatProjectListResponse getChatProjects(Long memberId) {

        return ChatProjectListResponse.builder()
                .projects(
                        projectMemberRepository.findByMemberId(memberId)
                                .stream()
                                .map(projectMember -> {

                                    Project project =
                                            projectMember.getProject();

                                    return ChatProjectListResponse.ProjectResponse
                                            .builder()
                                            .projectId(project.getId())
                                            .projectIconUrl(project.getIconUrl())
                                            .title(project.getName())
                                            .build();
                                })
                                .toList()
                )
                .build();
    }
    @Transactional(readOnly = true)
    public ChatRoomResponse getChatRoom(Long projectId) {

        ChatRoom chatRoom = chatRoomRepository.findByProjectId(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 프로젝트의 채팅방이 존재하지 않습니다."));

        return ChatRoomResponse.from(chatRoom);
    }
    @Transactional
    public ChatMessageResponse sendMessage(
            Long roomId,
            Long memberId,
            ChatMessageRequest request
    ) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다."));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(member)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        return ChatMessageResponse.from(savedMessage);
    }
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long roomId) {

        if (!chatRoomRepository.existsById(roomId)) {
            throw new IllegalArgumentException("존재하지 않는 채팅방입니다.");
        }

        return chatMessageRepository
                .findByChatRoomIdOrderByCreatedAtAsc(roomId)
                .stream()
                .map(ChatMessageResponse::from)
                .toList();
    }
    @Transactional
    public ChatRoomResponse createChatRoom(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        if (chatRoomRepository.findByProjectId(projectId).isPresent()) {
            throw new IllegalStateException("이미 채팅방이 존재합니다.");
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .project(project)
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomResponse.from(savedChatRoom);
    }
}