package com.fitple.fitple.controller;

import com.fitple.fitple.dto.response.TodayTaskResponse;
import com.fitple.fitple.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 홈 화면의 "오늘의 과제" 섹션 전용 조회 API.
 * 과제 생성/상태변경 등은 팀원이 만든 TaskController, TaskService를 그대로 사용하고,
 * 여기서는 "마감이 지나지 않은 과제만" 골라서 D-day와 함께 보여주는 조회 기능만 추가한다.
 */
@RestController
@RequiredArgsConstructor
public class TodayTaskController {

    private final TaskService taskService;

    @Operation(summary = "오늘의 과제 조회 (홈 화면용)", description = "로그인한 회원의 과제 중 마감이 지나지 않은 것만 D-day 임박순으로 조회합니다.")
    @GetMapping("/api/tasks/today")
    public ResponseEntity<List<TodayTaskResponse>> getTodayTasks(
            @Parameter(description = "로그인 회원 ID") @RequestParam Long memberId
    ) {
        return ResponseEntity.ok(taskService.getTodayTasks(memberId));
    }
}