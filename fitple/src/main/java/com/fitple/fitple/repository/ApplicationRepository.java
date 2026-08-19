package com.fitple.fitple.repository;

import com.fitple.fitple.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByProjectId(Long projectId);

    List<Application> findByMemberId(Long memberId);

    // 중복지원 확인용: 같은 회원이 같은 프로젝트에 PENDING 또는 ACCEPTED 상태로 이미 지원했는지
    boolean existsByProjectIdAndMemberIdAndStatusIn(
            Long projectId,
            Long memberId,
            List<Application.ApplicationStatus> statuses
    );
}