package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roadmap_stage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 프로젝트의 로드맵인지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 로드맵 버전
    @Column(nullable = false)
    private Integer roadmapVersion;

    // 단계 번호
    @Column(nullable = false)
    private Integer stageNumber;

    // 단계 제목
    @Column(nullable = false, length = 200)
    private String title;

    // 단계 설명
    @Column(length = 1000)
    private String description;

    // 시작일
    @Column(nullable = false)
    private LocalDate startDate;

    // 종료일
    @Column(nullable = false)
    private LocalDate endDate;

    // 단계 담당 팀원
    @ManyToMany
    @JoinTable(
            name = "roadmap_stage_member",
            joinColumns = @JoinColumn(name = "stage_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private List<Member> assignees = new ArrayList<>();
}