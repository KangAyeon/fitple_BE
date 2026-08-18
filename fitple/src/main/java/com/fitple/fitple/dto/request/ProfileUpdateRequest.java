package com.fitple.fitple.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProfileUpdateRequest(
        String profileImage,
        @NotBlank String name,
        @NotBlank String profileSummary
) {}