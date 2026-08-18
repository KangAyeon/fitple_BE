package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.RoadmapStage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Builder
public class RoadmapStageResponse {

    private Long stageId;
    private Integer stageNumber;
    private String title;
    private String description;

    private List<AssigneeResponse> assignees;

    private LocalDate startDate;
    private LocalDate endDate;

    private Long dDay;

    public static RoadmapStageResponse from(
            RoadmapStage stage,
            LocalDate today
    ) {
        long dDay = ChronoUnit.DAYS.between(
                today,
                stage.getEndDate()
        );

        return RoadmapStageResponse.builder()
                .stageId(stage.getId())
                .stageNumber(stage.getStageNumber())
                .title(stage.getTitle())
                .description(stage.getDescription())
                .assignees(
                        stage.getAssignees()
                                .stream()
                                .map(member ->
                                        new AssigneeResponse(
                                                member.getId(),
                                                member.getName()
                                        )
                                )
                                .toList()
                )
                .startDate(stage.getStartDate())
                .endDate(stage.getEndDate())
                .dDay(dDay)
                .build();
    }

    public record AssigneeResponse(
            Long memberId,
            String name
    ) {
    }
}