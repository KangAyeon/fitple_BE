package com.fitple.fitple.repository;

import com.fitple.fitple.domain.Introduction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntroductionRepository
        extends JpaRepository<Introduction, Long> {

    List<Introduction> findByMemberId(Long memberId);

    Optional<Introduction> findByIdAndMemberId(
            Long introductionId,
            Long memberId
    );
}

