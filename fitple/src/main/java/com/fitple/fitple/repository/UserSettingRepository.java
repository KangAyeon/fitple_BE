package com.fitple.fitple.repository;

import com.fitple.fitple.domain.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingRepository
        extends JpaRepository<UserSetting, Long> {

    Optional<UserSetting> findByMemberId(Long memberId);
}