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

import com.fitple.fitple.domain.Member;
import jakarta.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signin")
    public SigninResponse signin(
            @Valid @RequestBody SigninRequest request,
            HttpSession session
    ) {
        SigninResponse response = memberService.signin(request);

        if (response.isSuccess()) {
            Member member = memberService.getMemberByLoginId(request.getLoginId());
            session.setAttribute("memberId", member.getId());
        }

        return response;
    }
}
