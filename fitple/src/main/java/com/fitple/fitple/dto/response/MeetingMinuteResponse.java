package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.MeetingMinute;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingMinuteResponse {

    private Long meetingMinuteId;
    private Long projectId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static MeetingMinuteResponse from(
            MeetingMinute meetingMinute
    ) {
        return MeetingMinuteResponse.builder()
                .meetingMinuteId(meetingMinute.getId())
                .projectId(meetingMinute.getProject().getId())
                .title(meetingMinute.getTitle())
                .content(meetingMinute.getContent())
                .createdAt(meetingMinute.getCreatedAt())
                .build();
    }
}