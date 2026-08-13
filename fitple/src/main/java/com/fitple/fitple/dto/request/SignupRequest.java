package com.fitple.fitple.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이름을 입력하여 주십시오.")
    private String name;

    @NotBlank(message = "아이디을 입력하여 주십시오.")
    @Pattern(
            regexp = "^[A-Za-z0-9]{6,12}$",
            message = "아이디는 6-12자 영문, 숫자로 입력하여 주십시오."
    )
    private String loginId;

    @NotBlank(message = "비밀번호을 입력하여 주십시오.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[,!@#$%])[A-Za-z\\d,!@#$%]{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자(,!@#$%)를 혼합하여 8-20자로 입력하여 주십시오."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력하여 주십시오.")
    private String passwordConfirm;
}