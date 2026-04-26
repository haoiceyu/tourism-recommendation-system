package com.tourism.web;

import com.tourism.dto.CreateOrderRequest;
import com.tourism.dto.OrderDto;
import com.tourism.security.SecurityUtil;
import com.tourism.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderDto create(@Valid @RequestBody CreateOrderRequest req) {
        return orderService.create(SecurityUtil.currentUser().id(), req);
    }

    @GetMapping
    public List<OrderDto> list() {
        return orderService.listMine(SecurityUtil.currentUser().id());
    }

    @GetMapping("/{id}")
    public OrderDto get(@PathVariable Long id) {
        return orderService.get(SecurityUtil.currentUser().id(), id);
    }

    @PostMapping("/{id}/pay")
    public OrderDto pay(@PathVariable Long id) {
        return orderService.pay(SecurityUtil.currentUser().id(), id);
    }

    @PostMapping("/{id}/cancel")
    public OrderDto cancel(@PathVariable Long id) {
        return orderService.cancel(SecurityUtil.currentUser().id(), id);
    }

    @PostMapping("/{id}/complete")
    public OrderDto complete(@PathVariable Long id) {
        return orderService.complete(SecurityUtil.currentUser().id(), id);
    }
}
