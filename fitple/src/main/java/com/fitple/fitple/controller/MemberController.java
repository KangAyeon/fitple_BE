package com.fitple.fitple.controller;

import com.fitple.fitple.dto.request.SigninRequest;
import com.fitple.fitple.dto.request.SignupRequest;
import com.fitple.fitple.dto.response.CheckIdResponse;
import com.fitple.fitple.dto.response.SigninResponse;
import com.fitple.fitple.dto.response.SignupResponse;
import com.fitple.fitple.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberService memberService;

//    @PostMapping("/signup")
//    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
//        return memberService.signup(request);
//    }
}
