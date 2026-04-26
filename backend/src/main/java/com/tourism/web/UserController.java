package com.tourism.web;

import com.tourism.dto.ChangePasswordRequest;
import com.tourism.dto.UpdateProfileRequest;
import com.tourism.dto.UserProfileDto;
import com.tourism.security.AuthUser;
import com.tourism.security.SecurityUtil;
import com.tourism.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileDto me() {
        AuthUser u = SecurityUtil.currentUser();
        return userService.getProfile(u.id());
    }

    @PutMapping("/me")
    public UserProfileDto update(@Valid @RequestBody UpdateProfileRequest req) {
        AuthUser u = SecurityUtil.currentUser();
        return userService.updateProfile(u.id(), req);
    }

    @PutMapping("/me/password")
    public void password(@Valid @RequestBody ChangePasswordRequest req) {
        AuthUser u = SecurityUtil.currentUser();
        userService.changePassword(u.id(), req);
    }
}
