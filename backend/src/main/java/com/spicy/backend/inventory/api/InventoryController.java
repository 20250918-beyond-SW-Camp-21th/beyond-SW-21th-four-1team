package com.spicy.backend.inventory.api;

import com.spicy.backend.global.common.ApiResponse;
import com.spicy.backend.inventory.application.InventoryService;
import com.spicy.backend.inventory.dto.request.InventoryOutboundRequest;
import com.spicy.backend.inventory.dto.request.InventoryRequest;
import com.spicy.backend.inventory.dto.response.ProductResponse;
import com.spicy.backend.inventory.dto.response.ProductSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    @Operation(summary = "전체 상품 조회", description = "모든 상품에 대한 정보 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<ProductResponse>> getAllProduct() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllProduct()));
    }

    @Operation(summary = "상품 id로 상품 조회", description = "id로 상품에 대한 정보 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductSummaryResponse>> searchProduct(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.searchProduct(id)));
    }

    @Operation(summary = "상품 이름으로 상품 조회", description = "상품 이름으로 상품에 대한 정보 조회")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> searchByName(
            @RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.searchByName(name)));
    }

    @PostMapping("/inbound")
    @Operation(summary = "재고 입고", description = "상품에 재고를 입고한다")
    public ResponseEntity<ApiResponse<Void>> inbound(
            @Valid @RequestBody InventoryRequest inventoryRequest) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.inbound(inventoryRequest)));
    }

    @PostMapping("/outbound")
    @Operation(summary = "재고 차감", description = "상품의 재고를 차감한다")
    public ResponseEntity<ApiResponse<Void>> outbound(
            @Valid @RequestBody InventoryOutboundRequest inventoryOutboundRequest) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.outbound(inventoryOutboundRequest)));
    }

}
