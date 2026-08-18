package com.fitple.fitple.repository;

import com.fitple.fitple.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByMemberId(Long memberId);

    List<ProjectMember> findByProjectId(Long projectId);

    boolean existsByProjectIdAndMemberId(Long projectId, Long memberId);

    Optional<ProjectMember> findByProjectIdAndMemberId(
            Long projectId,
            Long memberId
    );

    void deleteByProjectId(Long projectId);
}