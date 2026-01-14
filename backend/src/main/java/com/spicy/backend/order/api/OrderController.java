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
    @PostMapping("/{user-id}/{store-id}")
    public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(
            @PathVariable("user-id") Long userId,
            @PathVariable("store-id") Long storeId, // 가맹점 식별 번호
            @RequestBody OrderCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(orderService.createOrder(storeId, userId, request)));
    }

    // 주문 조회
    @Operation(summary = "주문 조회", description = "가맹점주의 요청에 따라 전체, 완료, 취소된 주문 조회")
    @GetMapping("/{status}/{store-id}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(
            @PathVariable("store-id") Long storeId, // 가맹점 식별 번호
            @PathVariable Status status) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(storeId, status)));
    }

    // 주문 상세 조회
    @Operation(summary = "주문 상세 조회", description = "해당 주문의 상세 정보 조회")
    @GetMapping("/{store-id}/{order-id}/details")
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getOrderDetails(
            @PathVariable("store-id") Long storeId,
            @PathVariable("order-id") Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderDetails(storeId, orderId)));
    }

    // 주문 취소
    @Operation(summary = "주문 취소", description = "가맹점주가 생성했던 주문 중 요청받은 건들에 대해 취소를 진행")
    @PatchMapping("/{store-id}/{order-id}")
    public ResponseEntity<ApiResponse<OrderCanceledResponse>> cancelOrders(
            @PathVariable("store-id") Long storeId, // 가맹점 식별 번호
            @PathVariable("order-id") Long orderId) {

        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(storeId, orderId)));
    }
}
