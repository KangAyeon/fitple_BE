package com.fitple.fitple.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoadmapResponse {

    private Integer roadmapVersion;
    private List<RoadmapStageResponse> steps;
}