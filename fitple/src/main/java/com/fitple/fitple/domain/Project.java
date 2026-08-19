package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String introText;

    private Integer recruitCount;

    // 콤마구분 문자열로 저장 (예: "기획,촬영,영상 편집")
    private String roles;

    private LocalDate periodEnd;

    private String meetingSchedule;

    private LocalDate deadline;

    private String imageUrl;

    // 초대 QR/링크에 사용되는 고유 코드. 프로젝트 생성 시 자동 생성되어 저장된다.
    @Column(unique = true, length = 20)
    private String inviteCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.RECRUITING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum ProjectStatus {
        RECRUITING, IN_PROGRESS, CLOSED
    }

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String iconUrl;

    @Builder.Default
    private boolean isRecruiting = true;

    private LocalDate recruitDeadline;

}