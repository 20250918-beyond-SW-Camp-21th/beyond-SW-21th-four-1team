package com.spicy.backend.order.api;

import com.spicy.backend.global.common.ApiResponse;
import com.spicy.backend.order.application.OrderService;
import com.spicy.backend.order.dto.request.OrderCreateRequest;
import com.spicy.backend.order.dto.response.OrderCanceledResponse;
import com.spicy.backend.order.dto.response.OrderCreateResponse;
import com.spicy.backend.order.dto.response.OrderItemResponse;
import com.spicy.backend.order.dto.response.OrderResponse;
import com.spicy.backend.order.enums.Status;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @Operation(summary = "주문 생성", description = "가맹점주로부터 데이터를 전달받아 주문 생성")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{store-id}")
    public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(
            Authentication authentication,
            @PathVariable("store-id") Long storeId, // 가맹점 식별 번호
            @RequestBody OrderCreateRequest request) {
        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(ApiResponse.success(orderService.createOrder(storeId, userId, request)));
    }

    // 주문 조회
    @Operation(summary = "주문 조회", description = "가맹점주의 요청에 따라 전체, 완료, 취소된 주문 조회")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{status}/{store-id}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(
            @PathVariable("store-id") Long storeId, // 가맹점 식별 번호
            @PathVariable Status status,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(userId, storeId, status)));
    }

    // 주문 상세 조회
    @Operation(summary = "주문 상세 조회", description = "해당 주문의 상세 정보 조회")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{store-id}/{order-id}/details")
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getOrderDetails(
            Authentication authentication,
            @PathVariable("store-id") Long storeId,
            @PathVariable("order-id") Long orderId) {
        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderDetails(userId, storeId, orderId)));
    }

    // 주문 취소
    @Operation(summary = "주문 취소", description = "가맹점주가 생성했던 주문 중 요청받은 건들에 대해 취소를 진행")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{store-id}/{order-id}")
    public ResponseEntity<ApiResponse<OrderCanceledResponse>> cancelOrders(
            Authentication authentication,
            @PathVariable("store-id") Long storeId, // 가맹점 식별 번호
            @PathVariable("order-id") Long orderId) {
        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(userId, storeId, orderId)));
    }
}
