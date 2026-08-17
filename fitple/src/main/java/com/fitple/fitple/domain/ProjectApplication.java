package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_application",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "project_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 지원한 회원
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 지원한 프로젝트
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 지원 상태
    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "지원완료";

    // 지원일
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now();
}