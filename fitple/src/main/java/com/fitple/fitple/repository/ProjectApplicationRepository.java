package com.fitple.fitple.repository;

import com.fitple.fitple.domain.ProjectApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectApplicationRepository
        extends JpaRepository<ProjectApplication, Long> {

    List<ProjectApplication> findByMemberId(Long memberId);
}