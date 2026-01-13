package com.spicy.backend.order.api;

import com.spicy.backend.global.common.ApiResponse;
import com.spicy.backend.order.application.CartItemService;
import com.spicy.backend.order.dto.request.CartItemCreateRequest;
import com.spicy.backend.order.dto.response.CartItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    // 장바구니 추가
    @Operation(summary = "장바구니 추가", description = "가맹점주가 품목 장바구니에 추가")
    @PostMapping("/{user-id}/{store-id}")
    public ResponseEntity<ApiResponse<List<Long>>> addCartItem(
            @PathVariable("user-id") Long userId,    // 가맹점주
            @PathVariable("store-id") Long storeId,
            @RequestBody List<CartItemCreateRequest> request
    ) {
        // 사용자 검증 추가
        return ResponseEntity.ok(ApiResponse.success(cartItemService.addCartItem(userId, storeId, request)));
    }

    // 장바구니 조회
    @Operation(summary = "장바구니 조회", description = "가맹점주가 장바구니에 추가한 목록 조회")
    @GetMapping("/{user-id}/{store-id}")
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCartItems(
            @PathVariable("user-id") Long userId,    // 가맹점주
            @PathVariable("store-id") Long storeId
    ) {
        return ResponseEntity.ok(ApiResponse.success(cartItemService.getCartItems(userId, storeId)));
    }

    // 장바구니에서 상품 삭제
    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 선택한 상품들 삭제")
    @DeleteMapping("/{user-id}/{store-id}/{cart-item-id}")
    public ResponseEntity<ApiResponse<String>> deleteCartItems(
            @PathVariable("user-id") Long userId,    // 가맹점주
            @PathVariable("cart-item-id") Long cartItemId,
            @PathVariable("store-id") Long storeId
    ) {
        cartItemService.deleteCartItem(userId, cartItemId, storeId);

        return ResponseEntity.ok(ApiResponse.success("삭제 완료"));
    }
}
