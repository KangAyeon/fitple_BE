package com.fitple.fitple.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class ProjectCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String iconUrl;

    @Size(max = 2000)
    private String description;

}