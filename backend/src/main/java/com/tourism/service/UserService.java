package com.tourism.service;

import com.tourism.dto.ChangePasswordRequest;
import com.tourism.dto.UpdateProfileRequest;
import com.tourism.dto.UserProfileDto;
import com.tourism.entity.User;
import com.tourism.exception.BadRequestException;
import com.tourism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileDto getProfile(Long userId) {
        User u = userRepository.findById(userId).orElseThrow();
        return toDto(u);
    }

    @Transactional
    public UserProfileDto updateProfile(Long userId, UpdateProfileRequest req) {
        User u = userRepository.findById(userId).orElseThrow();
        if (req.nickname() != null) {
            u.setNickname(req.nickname());
        }
        if (req.email() != null && !req.email().equals(u.getEmail())) {
            if (userRepository.findByEmail(req.email()).isPresent()) {
                throw new BadRequestException("邮箱已被使用");
            }
            u.setEmail(req.email());
        }
        if (req.phone() != null) {
            u.setPhone(req.phone());
        }
        userRepository.save(u);
        return toDto(u);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        User u = userRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(req.oldPassword(), u.getPasswordHash())) {
            throw new BadRequestException("原密码不正确");
        }
        u.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(u);
    }

    private static UserProfileDto toDto(User u) {
        return new UserProfileDto(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getNickname(),
                u.getPhone(),
                u.getRole(),
                u.getCreatedAt()
        );
    }
}
