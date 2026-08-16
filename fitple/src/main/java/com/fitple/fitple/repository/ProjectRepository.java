package com.fitple.fitple.repository;

import com.fitple.fitple.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}