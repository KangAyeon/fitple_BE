package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "introduction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Introduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 역량을 작성한 회원
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 역량 제목
    @Column(nullable = false, length = 100)
    private String title;

    // 역량 상세 내용
    @Column(length = 2000)
    private String content;
}

