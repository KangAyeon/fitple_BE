package com.fitple.fitple.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScheduleUpdateRequests {

    private List<ScheduleUpdateRequest> updates;
}