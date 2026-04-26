package com.tourism.service;

import com.tourism.domain.OrderStatus;
import com.tourism.domain.ProductType;
import com.tourism.dto.*;
import com.tourism.entity.*;
import com.tourism.exception.BadRequestException;
import com.tourism.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final TourOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AttractionRepository attractionRepository;
    private final HotelRepository hotelRepository;
    private final RecommendationService recommendationService;

    @Transactional
    public OrderDto create(Long userId, CreateOrderRequest req) {
        BigDecimal total = BigDecimal.ZERO;
        record Line(ProductType type, Long pid, String title, BigDecimal unit, int qty, BigDecimal sub) {}
        List<Line> computed = new ArrayList<>();
        for (OrderLineRequest line : req.items()) {
            BigDecimal unit;
            String title;
            if (line.productType() == ProductType.ATTRACTION) {
                Attraction a = attractionRepository.findById(line.productId())
                        .orElseThrow(() -> new BadRequestException("景点不存在"));
                if (!a.isActive()) throw new BadRequestException("景点已下架");
                unit = a.getPrice();
                title = a.getName();
            } else {
                Hotel h = hotelRepository.findById(line.productId())
                        .orElseThrow(() -> new BadRequestException("酒店不存在"));
                if (!h.isActive()) throw new BadRequestException("酒店已下架");
                unit = h.getPricePerNight();
                title = h.getName();
            }
            BigDecimal sub = unit.multiply(BigDecimal.valueOf(line.quantity()));
            total = total.add(sub);
            computed.add(new Line(line.productType(), line.productId(), title, unit, line.quantity(), sub));
        }
        String orderNo = "TO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);
        TourOrder order = TourOrder.builder()
                .userId(userId)
                .orderNo(orderNo)
                .totalAmount(total)
                .status(OrderStatus.PENDING_PAYMENT)
                .createdAt(Instant.now())
                .build();
        order = orderRepository.save(order);
        for (Line line : computed) {
            OrderItem oi = OrderItem.builder()
                    .orderId(order.getId())
                    .productType(line.type())
                    .productId(line.pid())
                    .title(line.title())
                    .unitPrice(line.unit())
                    .quantity(line.qty())
                    .subtotal(line.sub())
                    .build();
            orderItemRepository.save(oi);
        }
        recommendationService.evictUserCache(userId);
        return get(userId, order.getId());
    }

    public List<OrderDto> listMine(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(o -> toDto(o, orderItemRepository.findByOrderId(o.getId())))
                .collect(Collectors.toList());
    }

    public OrderDto get(Long userId, Long orderId) {
        TourOrder o = orderRepository.findById(orderId).orElseThrow(() -> new BadRequestException("订单不存在"));
        if (!o.getUserId().equals(userId)) {
            throw new BadRequestException("无权访问该订单");
        }
        return toDto(o, orderItemRepository.findByOrderId(orderId));
    }

    public OrderDto getForAdmin(Long orderId) {
        TourOrder o = orderRepository.findById(orderId).orElseThrow(() -> new BadRequestException("订单不存在"));
        return toDto(o, orderItemRepository.findByOrderId(orderId));
    }

    public List<OrderDto> listAll() {
        return orderRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(o -> toDto(o, orderItemRepository.findByOrderId(o.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto pay(Long userId, Long orderId) {
        TourOrder o = lockOrder(userId, orderId);
        if (o.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BadRequestException("订单状态不允许支付");
        }
        o.setStatus(OrderStatus.PAID);
        o.setPaidAt(Instant.now());
        orderRepository.save(o);
        recommendationService.evictUserCache(userId);
        return get(userId, orderId);
    }

    @Transactional
    public OrderDto cancel(Long userId, Long orderId) {
        TourOrder o = lockOrder(userId, orderId);
        if (o.getStatus() == OrderStatus.COMPLETED || o.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("订单无法取消");
        }
        o.setStatus(OrderStatus.CANCELLED);
        o.setCancelledAt(Instant.now());
        orderRepository.save(o);
        recommendationService.evictUserCache(userId);
        return get(userId, orderId);
    }

    @Transactional
    public OrderDto complete(Long userId, Long orderId) {
        TourOrder o = lockOrder(userId, orderId);
        if (o.getStatus() != OrderStatus.PAID) {
            throw new BadRequestException("仅已支付订单可确认完成");
        }
        o.setStatus(OrderStatus.COMPLETED);
        o.setCompletedAt(Instant.now());
        orderRepository.save(o);
        recommendationService.evictUserCache(userId);
        return get(userId, orderId);
    }

    private TourOrder lockOrder(Long userId, Long orderId) {
        TourOrder o = orderRepository.findById(orderId).orElseThrow(() -> new BadRequestException("订单不存在"));
        if (!o.getUserId().equals(userId)) {
            throw new BadRequestException("无权操作该订单");
        }
        return o;
    }

    private OrderDto toDto(TourOrder o, List<OrderItem> items) {
        List<OrderItemDto> dtos = items.stream()
                .map(i -> new OrderItemDto(
                        i.getId(),
                        i.getProductType(),
                        i.getProductId(),
                        i.getTitle(),
                        i.getUnitPrice(),
                        i.getQuantity(),
                        i.getSubtotal()
                ))
                .collect(Collectors.toList());
        return new OrderDto(
                o.getId(),
                o.getOrderNo(),
                o.getTotalAmount(),
                o.getStatus(),
                o.getCreatedAt(),
                o.getPaidAt(),
                o.getCompletedAt(),
                o.getCancelledAt(),
                dtos
        );
    }
}
