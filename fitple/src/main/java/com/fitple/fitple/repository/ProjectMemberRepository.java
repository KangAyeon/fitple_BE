package com.fitple.fitple.repository;

import com.fitple.fitple.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByMemberId(Long memberId);

    List<ProjectMember> findByProjectId(Long projectId);
}