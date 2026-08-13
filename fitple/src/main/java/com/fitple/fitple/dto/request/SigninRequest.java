package com.fitple.fitple.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SigninRequest {

    @NotBlank(message = "아이디을 입력하여 주십시오.")
    @JsonProperty("login_id")
    private String loginId;

    // 개인적으로 매번 비밀번호 유형 헷갈린 경험이 있어서 늘려 적었습니다.
    @NotBlank(message = "비밀번호을 입력하여 주십시오. 영문, 숫자, 특문을 포함합니다")
    private String password;
}