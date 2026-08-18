package com.fitple.fitple.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdCheckResponse {

    private boolean available;
    private String message;
}