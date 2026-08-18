package com.fitple.fitple.repository;

import com.fitple.fitple.domain.RoadmapStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapStageRepository
        extends JpaRepository<RoadmapStage, Long> {

    List<RoadmapStage> findByProjectIdAndRoadmapVersionOrderByStageNumberAsc(
            Long projectId,
            Integer roadmapVersion
    );

    List<RoadmapStage> findByProjectIdOrderByRoadmapVersionDescStageNumberAsc(
            Long projectId
    );
}