package com.fitple.fitple.controller;

import com.fitple.fitple.dto.request.SigninRequest;
import com.fitple.fitple.dto.request.SignupRequest;
import com.fitple.fitple.dto.response.CheckIdResponse;
import com.fitple.fitple.dto.response.SigninResponse;
import com.fitple.fitple.dto.response.SignupResponse;
import com.fitple.fitple.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.fitple.fitple.domain.Member;
import jakarta.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberService memberService;

//    @PostMapping("/signin")
//    public SigninResponse signin(
//            @Valid @RequestBody SigninRequest request,
//            HttpSession session
//    ) {
//        SigninResponse response = memberService.signin(request);
//
//        if (response.isSuccess()) {
//            Member member = memberService.getMemberByLoginId(request.getLoginId());
//            session.setAttribute("memberId", member.getId());
//        }
//
//        return response;
//    }
    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    headers = {
                            @Header(
                                    name = "Set-Cookie",
                                    description = "로그인 세션 쿠키(JSESSIONID)",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                                            type = "string"
                                    )
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "아이디 또는 비밀번호가 올바르지 않습니다."
            )
    })
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
    @PostMapping("/logout")
    public SigninResponse logout(
            HttpSession session,
            HttpServletResponse response
    ) {
        session.invalidate();


        // 만약 쿠키 완전삭제가 문제된다면 아래 4줄 주석하면 됨 (response addCookie MaDe)
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return SigninResponse.builder()
                .success(true)
                .message("로그아웃되었습니다.")
                .build();
    }
    @PostMapping("/logoutYobi")
    public SigninResponse logoutYobi(
            HttpSession session,
            HttpServletResponse response
    ) {
        session.invalidate();


        // 만약 쿠키 완전삭제가 문제된다면 아래 4줄 주석 풀면 됨
//        Cookie cookie = new Cookie("JSESSIONID", null);
//        cookie.setPath("/");
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);

        return SigninResponse.builder()
                .success(true)
                .message("로그아웃되었습니다.")
                .build();
    }
}
