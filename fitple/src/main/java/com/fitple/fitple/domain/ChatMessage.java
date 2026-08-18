package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메시지가 속한 채팅방
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    // 메시지를 보낸 회원
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 메시지 내용
    @Column(nullable = false, length = 5000)
    private String content;

    // 메시지 작성 시간
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "chatMessage",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ChatFile> files = new ArrayList<>();
}