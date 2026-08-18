package com.fitple.fitple.controller;

import com.fitple.fitple.dto.response.AIRoleAssignResponse;
import com.fitple.fitple.service.AIRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIRoleController {

    private final AIRoleService aiRoleService;

    @PostMapping("/projects/{projectId}/roles")
    public AIRoleAssignResponse assignRoles(
            @PathVariable Long projectId
    ) {
        return aiRoleService.assignRoles(projectId);
    }
}