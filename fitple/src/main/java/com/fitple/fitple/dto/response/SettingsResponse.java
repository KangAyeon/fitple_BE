package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.UserSetting;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettingsResponse {

    private String fontSize;
    private boolean notificationEnabled;
    private boolean translationEnabled;

    public static SettingsResponse from(UserSetting setting) {
        return SettingsResponse.builder()
                .fontSize(setting.getFontSize().name())
                .notificationEnabled(setting.isNotificationEnabled())
                .translationEnabled(setting.isTranslationEnabled())
                .build();
    }
}