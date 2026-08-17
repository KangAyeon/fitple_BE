package com.fitple.fitple.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SettingsUpdateRequest {

    private String fontSize;
    private Boolean notificationEnabled;
}