package com.fitple.fitple.service;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.domain.UserSetting;
import com.fitple.fitple.dto.request.SettingsUpdateRequest;
import com.fitple.fitple.dto.response.SettingsResponse;
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingService {

    private final UserSettingRepository userSettingRepository;
    private final MemberRepository memberRepository;

    // 환경 설정 조회
    @Transactional
    public SettingsResponse getSettings(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다."));

        UserSetting setting =
                userSettingRepository.findByMemberId(memberId)
                        .orElseGet(() ->
                                userSettingRepository.save(
                                        UserSetting.builder()
                                                .member(member)
                                                .fontSize(UserSetting.FontSize.MEDIUM)
                                                .notificationEnabled(true)
                                                .translationEnabled(true)
                                                .build()
                                )
                        );

        return SettingsResponse.from(setting);
    }

    // 환경 설정 변경
    @Transactional
    public SettingsResponse updateSettings(
            Long memberId,
            SettingsUpdateRequest request
    ) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다."));

        UserSetting setting =
                userSettingRepository.findByMemberId(memberId)
                        .orElseGet(() ->
                                userSettingRepository.save(
                                        UserSetting.builder()
                                                .member(member)
                                                .fontSize(UserSetting.FontSize.MEDIUM)
                                                .notificationEnabled(true)
                                                .translationEnabled(true)
                                                .build()
                                )
                        );

        if (request.getFontSize() != null) {
            try {
                setting.setFontSize(
                        UserSetting.FontSize.valueOf(
                                request.getFontSize().toUpperCase()
                        )
                );
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "지원하지 않는 글자 크기입니다."
                );
            }
        }

        if (request.getNotificationEnabled() != null) {
            setting.setNotificationEnabled(
                    request.getNotificationEnabled()
            );
        }

        if (request.getTranslationEnabled() != null) {
            setting.setTranslationEnabled(
                    request.getTranslationEnabled()
            );
        }

        return SettingsResponse.from(setting);
    }
}