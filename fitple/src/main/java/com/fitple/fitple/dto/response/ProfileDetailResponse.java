package com.fitple.fitple.dto.response;

public record ProfileDetailResponse(
        String profileImage,
        String name,
        String profileSummary
) {}