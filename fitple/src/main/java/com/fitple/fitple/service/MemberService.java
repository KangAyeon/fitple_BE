package com.fitple.fitple.service;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.dto.request.SigninRequest;
import com.fitple.fitple.dto.request.SignupRequest;
import com.fitple.fitple.dto.response.CheckIdResponse;
import com.fitple.fitple.dto.response.SigninResponse;
import com.fitple.fitple.dto.response.SignupResponse;
import com.fitple.fitple.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public SignupResponse signup(SignupRequest request) {

        // ID checking Jung-bok
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // PW checking
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // PW encoding
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Member OBJ generationg
        Member member = Member.builder()
                .name(request.getName())
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .build();

        // SAVING
        memberRepository.save(member);

        // returning : response
        return SignupResponse.builder()
                .success(true)
                .message("회원가입이 완료되었습니다.")
                .build();
    }

    public CheckIdResponse checkId(String loginId) {

        boolean available = !memberRepository.existsByLoginId(loginId);

        if (available) {
            return CheckIdResponse.builder()
                    .success(true)
                    .message("사용 가능한 아이디입니다.")
                    .available(true)
                    .build();
        }

        return CheckIdResponse.builder()
                .success(true)
                .message("이미 사용 중인 아이디입니다.")
                .available(false)
                .build();

    }

//    public SigninResponse signin(SigninRequest request, HttpSession session) {
//
//        Member member = memberRepository.findByLoginId(request.getLoginId())
//                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));
//
//        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
//            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
//        }
//
//        session.setAttribute("memberId", member.getId());
//
//        return SigninResponse.builder()
//                .success(true)
//                .message("로그인에 성공했습니다.")
//                .build();
//    }

    public SigninResponse signin(SigninRequest request) {

        // ID zo-hoi
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        // PW checking
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // LOGIN success
        return SigninResponse.builder()
                .success(true)
                .memberId(member.getId())
                .message("로그인에 성공했습니다.")
                .build();
    }
    public Member getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}

