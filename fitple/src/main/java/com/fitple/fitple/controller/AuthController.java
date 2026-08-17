package com.fitple.fitple.controller;

import com.fitple.fitple.dto.request.SignupRequest;
import com.fitple.fitple.dto.response.IdCheckResponse;
import com.fitple.fitple.dto.response.SignupResponse;
import com.fitple.fitple.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = authService.signup(request);

        if (!response.isSuccess()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/check_id")
    public IdCheckResponse checkLoginId(
            @RequestParam("login_id") String loginId
    ) {
        return authService.checkLoginId(loginId);
    }
}