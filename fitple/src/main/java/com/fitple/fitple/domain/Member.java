package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

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
}