package com.tourism.web;

import com.tourism.dto.DashboardStatsDto;
import com.tourism.dto.OrderDto;
import com.tourism.dto.UpdateRoleRequest;
import com.tourism.dto.UserProfileDto;
import com.tourism.service.AdminService;
import com.tourism.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final OrderService orderService;

    @GetMapping("/dashboard/stats")
    public DashboardStatsDto dashboard() {
        return adminService.dashboard();
    }

    @GetMapping("/users")
    public List<UserProfileDto> users() {
        return adminService.listUsers();
    }

    @PutMapping("/users/{id}/role")
    public UserProfileDto role(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest req) {
        return adminService.updateRole(id, req.role());
    }

    @GetMapping("/orders")
    public List<OrderDto> orders() {
        return orderService.listAll();
    }

    @GetMapping("/orders/{id}")
    public OrderDto order(@PathVariable Long id) {
        return orderService.getForAdmin(id);
    }
}
