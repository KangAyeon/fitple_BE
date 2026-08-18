package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_recruit_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRecruitRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 모집 중인 프로젝트
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 모집 역할
    @Column(nullable = false, length = 50)
    private String role;
}