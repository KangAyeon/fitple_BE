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

import com.fitple.fitple.domain.ChatMessage;
import org.springframework.data.domain.PageRequest;

import java.util.Comparator;
import java.util.List;

import com.fitple.fitple.domain.ChatFile;
import com.fitple.fitple.repository.ChatFileRepository;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.fitple.fitple.domain.MeetingMinute;
import com.fitple.fitple.dto.request.MeetingMinuteCreateRequest;
import com.fitple.fitple.dto.response.MeetingMinuteResponse;
import com.fitple.fitple.repository.MeetingMinuteRepository;

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

    private final ChatFileRepository chatFileRepository;

    private final MeetingMinuteRepository meetingMinuteRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

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
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getPreviousMessages(
            Long roomId,
            int size
    ) {

        if (!chatRoomRepository.existsById(roomId)) {
            throw new IllegalArgumentException("존재하지 않는 채팅방입니다.");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("size는 1 이상이어야 합니다.");
        }

        List<ChatMessage> messages =
                chatMessageRepository
                        .findTopByChatRoomIdOrderByCreatedAtDesc(
                                roomId,
                                PageRequest.of(0, size)
                        );

        return messages.stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .map(ChatMessageResponse::from)
                .toList();
    }
    @Transactional
    public void uploadFile(
            Long projectId,
            Long memberId,
            MultipartFile file
    ) {

        ChatRoom chatRoom = chatRoomRepository.findByProjectId(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하는 채팅방이 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }

        try {
            Path uploadPath = Paths.get(
                    System.getProperty("user.dir"),
                    "uploads",
                    "chat"
            );

            Files.createDirectories(uploadPath);

            String extension = "";

            int extensionIndex = originalFileName.lastIndexOf(".");

            if (extensionIndex >= 0) {
                extension = originalFileName.substring(extensionIndex);
            }

            String savedFileName =
                    UUID.randomUUID() + extension;

            Path targetPath =
                    uploadPath.resolve(savedFileName);

            file.transferTo(targetPath.toFile());

            String fileUrl =
                    "/uploads/chat/" + savedFileName;

            ChatMessage message = ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .member(member)
                    .content("")
                    .createdAt(LocalDateTime.now())
                    .build();

            ChatMessage savedMessage =
                    chatMessageRepository.save(message);

            ChatFile chatFile = ChatFile.builder()
                    .chatMessage(savedMessage)
                    .originalFileName(originalFileName)
                    .fileUrl(fileUrl)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

            chatFileRepository.save(chatFile);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "파일 저장에 실패했습니다.",
                    e
            );
        }
    }
    @Transactional
    public MeetingMinuteResponse createMeetingMinute(
            Long projectId,
            MeetingMinuteCreateRequest request
    ) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 프로젝트입니다."
                        ));

        MeetingMinute meetingMinute = MeetingMinute.builder()
                .project(project)
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        MeetingMinute savedMeetingMinute =
                meetingMinuteRepository.save(meetingMinute);

        return MeetingMinuteResponse.from(savedMeetingMinute);
    }

    @Transactional(readOnly = true)
    public List<MeetingMinuteResponse> getMeetingMinutes(
            Long projectId
    ) {

        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException(
                    "프로젝트가 있지 아니합니다."
            );
        }

        return meetingMinuteRepository
                .findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(MeetingMinuteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MeetingMinuteResponse getMeetingMinute(
            Long projectId,
            Long meetingMinuteId
    ) {

        MeetingMinute meetingMinute =
                meetingMinuteRepository.findById(meetingMinuteId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 회의록입니다."
                                ));

        if (!meetingMinute.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException(
                    "현재 프로젝트의 회의록이 아닙니다."
            );
        }

        return MeetingMinuteResponse.from(meetingMinute);
    }

}