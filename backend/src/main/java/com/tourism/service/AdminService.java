package com.tourism.service;

import com.tourism.domain.OrderStatus;
import com.tourism.domain.UserRole;
import com.tourism.dto.DashboardStatsDto;
import com.tourism.dto.UserProfileDto;
import com.tourism.entity.User;
import com.tourism.exception.BadRequestException;
import com.tourism.repository.TourOrderRepository;
import com.tourism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TourOrderRepository orderRepository;

    public DashboardStatsDto dashboard() {
        Instant start = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
        long newUsers = userRepository.countByCreatedAtAfter(start);
        BigDecimal rev = orderRepository.sumPaidAmountSince(start);
        if (rev == null) rev = BigDecimal.ZERO;
        return new DashboardStatsDto(
                userRepository.count(),
                newUsers,
                orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT),
                orderRepository.countByStatus(OrderStatus.PAID),
                orderRepository.countByStatus(OrderStatus.COMPLETED),
                orderRepository.countByStatus(OrderStatus.CANCELLED),
                rev
        );
    }

    public List<UserProfileDto> listUsers() {
        return userRepository.findAll().stream()
                .map(AdminService::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserProfileDto updateRole(Long userId, UserRole role) {
        User u = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("用户不存在"));
        u.setRole(role);
        userRepository.save(u);
        return toDto(u);
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
