package com.tourism.service;

import com.tourism.domain.UserRole;
import com.tourism.dto.*;
import com.tourism.entity.PasswordResetToken;
import com.tourism.entity.User;
import com.tourism.exception.BadRequestException;
import com.tourism.repository.PasswordResetTokenRepository;
import com.tourism.repository.UserRepository;
import com.tourism.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new BadRequestException("用户名已存在");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new BadRequestException("邮箱已被注册");
        }
        User u = User.builder()
                .username(req.username())
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .nickname(req.nickname() != null ? req.nickname() : req.username())
                .phone(null)
                .role(UserRole.USER)
                .createdAt(Instant.now())
                .build();
        u = userRepository.save(u);
        String token = jwtService.generateToken(u.getId(), u.getUsername(), u.getRole().name());
        return new AuthResponse(token, u.getId(), u.getUsername(), u.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        User u = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new BadRequestException("用户名或密码错误"));
        if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            throw new BadRequestException("用户名或密码错误");
        }
        String token = jwtService.generateToken(u.getId(), u.getUsername(), u.getRole().name());
        return new AuthResponse(token, u.getId(), u.getUsername(), u.getRole().name());
    }

    @Transactional
    public String requestPasswordReset(PasswordResetRequest req) {
        User u = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadRequestException("邮箱未注册"));
        String raw = UUID.randomUUID().toString().replace("-", "");
        PasswordResetToken t = PasswordResetToken.builder()
                .userId(u.getId())
                .token(raw)
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .used(false)
                .build();
        tokenRepository.save(t);
        return raw;
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest req) {
        PasswordResetToken t = tokenRepository.findByToken(req.token())
                .orElseThrow(() -> new BadRequestException("无效或已过期的重置令牌"));
        if (t.isUsed() || t.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("无效或已过期的重置令牌");
        }
        User u = userRepository.findById(t.getUserId()).orElseThrow();
        u.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(u);
        t.setUsed(true);
        tokenRepository.save(t);
    }
}
