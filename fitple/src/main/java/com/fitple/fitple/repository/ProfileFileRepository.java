package com.fitple.fitple.repository;

import com.fitple.fitple.domain.ProfileFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileFileRepository
        extends JpaRepository<ProfileFile, Long> {

    List<ProfileFile> findByMemberId(Long memberId);
}