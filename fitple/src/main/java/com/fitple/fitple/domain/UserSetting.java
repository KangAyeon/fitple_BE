package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_setting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 설정을 가진 회원
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    // 글자 크기
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FontSize fontSize;

    // 알림 활성화 여부
    @Column(nullable = false)
    private boolean notificationEnabled;

    public enum FontSize {
        SMALL,
        MEDIUM,
        LARGE
    }
}