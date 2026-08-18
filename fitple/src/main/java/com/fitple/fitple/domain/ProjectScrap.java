package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "project_scrap",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "project_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 스크랩한 회원
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 스크랩한 프로젝트
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}