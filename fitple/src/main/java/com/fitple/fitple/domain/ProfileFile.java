package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String originalName;

    private String contentType;

    private Long fileSize;
}