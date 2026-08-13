package com.fitple.fitple.service;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.dto.request.SignupRequest;
import com.fitple.fitple.dto.response.SignupResponse;
import com.fitple.fitple.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {

        // ID jung-bok
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            return SignupResponse.builder()
                    .success(false)
                    .message("이미 사용 중인 아이디입니다.")
                    .build();
        }

        // PW corrected?
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            return SignupResponse.builder()
                    .success(false)
                    .message("비밀번호가 일치하지 않습니다.")
                    .build();
        }

        // member gernating
        Member member = Member.builder()
                .name(request.getName())
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // DB SAVING
        memberRepository.save(member);

        // response : success
        return SignupResponse.builder()
                .success(true)
                .message("회원가입이 완료되었습니다.")
                .build();
    }
}