package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.RoadmapStage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ScheduleUpdateResponse {

    private Long stageId;

    private LocalDate previousStartDate;
    private LocalDate previousEndDate;

    private LocalDate newStartDate;
    private LocalDate newEndDate;

    private String reason;

    public static ScheduleUpdateResponse of(
            Long stageId,
            LocalDate previousStartDate,
            LocalDate previousEndDate,
            RoadmapStage stage,
            String reason
    ) {
        return ScheduleUpdateResponse.builder()
                .stageId(stageId)
                .previousStartDate(previousStartDate)
                .previousEndDate(previousEndDate)
                .newStartDate(stage.getStartDate())
                .newEndDate(stage.getEndDate())
                .reason(reason)
                .build();
    }
}