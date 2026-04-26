package com.tourism.web;

import com.tourism.dto.*;
import com.tourism.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/password-reset/request")
    public PasswordResetResponse requestReset(@Valid @RequestBody PasswordResetRequest req) {
        String token = authService.requestPasswordReset(req);
        return new PasswordResetResponse(token, "模拟邮件：请使用返回的 resetToken 调用确认接口（生产环境应发送邮件）");
    }

    @PostMapping("/password-reset/confirm")
    public void confirmReset(@Valid @RequestBody PasswordResetConfirmRequest req) {
        authService.confirmPasswordReset(req);
    }
}
