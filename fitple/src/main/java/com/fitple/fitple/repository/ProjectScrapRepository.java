package com.fitple.fitple.repository;

import com.fitple.fitple.domain.ProjectScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectScrapRepository
        extends JpaRepository<ProjectScrap, Long> {

    List<ProjectScrap> findByMemberId(Long memberId);

    boolean existsByMemberIdAndProjectId(
            Long memberId,
            Long projectId
    );

    void deleteByMemberIdAndProjectId(
            Long memberId,
            Long projectId
    );
}