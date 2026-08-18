package com.fitple.fitple.dto.response;

import com.fitple.fitple.domain.RoadmapStage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;

@Getter
@Builder
public class RoadmapStepResponse {

    private Integer stepNumber;
    private String title;
    private List<String> assignees;
    private String detail;
    private LocalDate date;
    private String dayOfWeek;
    private String dDay;


    public static RoadmapStepResponse from(
            RoadmapStage stage
    ) {
        return RoadmapStepResponse.builder()
                .stepNumber(stage.getStageNumber())
                .title(stage.getTitle())
                .assignees(
                        stage.getAssignees()
                                .stream()
                                .map(member -> member.getName())
                                .toList()
                )
                .detail(stage.getDescription())
                // 기존 API 명세에서는 date를 단계 시작일로 사용
                .date(stage.getStartDate())
                .dayOfWeek(
                        convertDayOfWeek(
                                stage.getStartDate().getDayOfWeek()
                        )
                )
                .dDay(
                        calculateDDay(stage.getStartDate())
                )
                .build();
    }

    private static String convertDayOfWeek(
            DayOfWeek dayOfWeek
    ) {
        return switch (dayOfWeek) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
            case SUNDAY -> "일";
        };
    }

    private static String calculateDDay(
            LocalDate date
    ) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(),
                date
        );

        if (days > 0) {
            return "D-" + days;
        }

        if (days == 0) {
            return "D-Day";
        }

        return "D+" + Math.abs(days);
    }
}