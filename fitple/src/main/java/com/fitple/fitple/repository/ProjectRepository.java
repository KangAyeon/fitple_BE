package com.fitple.fitple.repository;

import com.fitple.fitple.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(Project.ProjectStatus status);

    Optional<Project> findByInviteCode(String inviteCode);
}