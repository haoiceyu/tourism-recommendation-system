package com.tourism.web;

import com.tourism.domain.ProductType;
import com.tourism.security.SecurityUtil;
import com.tourism.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/behavior")
@RequiredArgsConstructor
public class BehaviorController {

    private final UserBehaviorService userBehaviorService;

    @PostMapping("/view")
    public void view(@RequestParam ProductType productType, @RequestParam Long productId) {
        userBehaviorService.recordView(SecurityUtil.currentUser().id(), productType, productId);
    }
}
