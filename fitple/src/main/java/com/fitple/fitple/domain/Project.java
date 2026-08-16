package com.fitple.fitple.domain;

import jakarta.persistence.*;
import lombok.*;

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

    // irum of project
    @Column(nullable = false, length = 100)
    private String name;

    // project icon Gyeong-ro
    @Column(length = 500)
    private String iconUrl;

    // recruiting yeo-bu
    @Column(nullable = false)
    private boolean recruiting;

    // project saeng-seong-il
    @Column(nullable = false)
    private java.time.LocalDateTime createdAt;

    @Column(length = 2000)
    private String description;

}