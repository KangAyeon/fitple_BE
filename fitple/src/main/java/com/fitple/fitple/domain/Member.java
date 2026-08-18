package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //seting i-rum (null nono)
    @Column(nullable = false, length = 20)
    private String name;

    //seting id (choidai: 12ja, no jungbok)
    @Column(nullable = false, unique = true, length = 12)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private boolean translationEnabled = true;

    private String profileImage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Introduction> introductions = new ArrayList<>();

    // 프로필 이미지 수정
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    // 최신 Introduction 객체 반환
    public Introduction getIntroduction() {
        if (this.introductions == null || this.introductions.isEmpty()) {
            return null;
        }
        return this.introductions.get(this.introductions.size() - 1);
    }

    // Introduction 생성 및 수정
    public void updateIntroduction(String content) {
        Introduction current = getIntroduction();
        if (current != null) {
            current.setContent(content);
        } else {
            Introduction newIntro = Introduction.builder()
                    .member(this)
                    .title("AI 프로필")
                    .content(content)
                    .build();
            this.introductions.add(newIntro);
        }
    }
    @OneToMany(
            mappedBy = "member",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ProfileFile> profileFiles = new ArrayList<>();

}
