package com.fitple.fitple.repository;

import com.fitple.fitple.domain.ProjectRecruitRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRecruitRoleRepository
        extends JpaRepository<ProjectRecruitRole, Long> {

    List<ProjectRecruitRole> findByProjectId(Long projectId);
}