package com.spicy.backend.inventory.application;

import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.inventory.domain.Inventory;
import com.spicy.backend.inventory.domain.LotStatus;
import com.spicy.backend.inventory.domain.MinimumProduct;
import com.spicy.backend.inventory.dto.request.InventoryOutboundRequest;
import com.spicy.backend.inventory.dto.request.InventoryRequest;
import com.spicy.backend.inventory.dto.response.InventoryLotResponse;
import com.spicy.backend.inventory.dto.response.ProductBaseInfo;
import com.spicy.backend.inventory.dto.response.ProductResponse;
import com.spicy.backend.inventory.dto.response.ProductSummaryResponse;
import com.spicy.backend.inventory.error.InventoryErrorCode;
import com.spicy.backend.inventory.storage.InventoryRepository;
import com.spicy.backend.inventory.storage.MinimumProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@RequiredArgsConstructor
public class InventoryService {
    // 가맹점 ID는 한 개로 해놓음
    private static final Long DEFAULT_STORE_ID = 1L;
    final private InventoryRepository inventoryRepository;
    final private MinimumProductRepository minimumProductRepository;

    public ProductResponse getAllProduct() {
        // 유통기한 업데이트
        updateExpiredInventoryStatus();

        // 인벤토리에 있는 InventoryLotResponse형태로 다 가져오기
        List<InventoryLotResponse> inventoryLotResponses = inventoryRepository.findAllProduct();

        // 없으면 예외처리
        if (inventoryLotResponses.isEmpty()) {
            throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
        }

        // productId에 맞춘 InventoryLotResponse HashMap생성
        HashMap<Long, List<InventoryLotResponse>> inventories = new HashMap<>();

        // 해쉬맵에 productId에 해당하는 값 넣기
        for (InventoryLotResponse inventoryLotResponse : inventoryLotResponses) {
            Long id = inventoryLotResponse.productId();

            // 해쉬맵에 해당하는 id가 없을 경우 id값과 배열 선언
            if (!inventories.containsKey(id)) {
                inventories.put(id, new ArrayList<>());
            }

            // 해당 id값에 상품 리스트 넣기
            inventories.get(id).add(inventoryLotResponse);

        }

        // 큰 단위 상품 리스트 만들기 (상품A in (상품A1,상품A2,,), 상품B in (상품B1,상품B2,,),,
        List<ProductSummaryResponse> productSummaries = new ArrayList<>();

        // productSummaries에 넣을 기본 값 가져오기 (여러 번 DB에 참조하는 것을 방지)
        // 가맹점 늘어날 시 Request로 가맹점 ID를 받아야 할 예정
        List<ProductBaseInfo> baseInfos = inventoryRepository.findAllProductBaseInfo();

        // 기본 값 해쉬맵 생성
        Map<Long, ProductBaseInfo> baseInfoMap = new HashMap<>();
        for (ProductBaseInfo info : baseInfos) {
            baseInfoMap.put(info.productId(), info);
        }

        // 해쉬맵에서 값 꺼내기
        for (Map.Entry<Long, List<InventoryLotResponse>> entry : inventories.entrySet()) {
            Long id = entry.getKey();
            // id에 알맞은 기본 정보를 꺼내옴
            ProductBaseInfo productBaseInfo = baseInfoMap.get(id);

            if (productBaseInfo == null) {
                throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
            }
            // 총 수량 계산
            int totalQuantity = 0;
            for (InventoryLotResponse inventoryLotResponse : entry.getValue()) {
                // 활성화 된 재고만 총 수량에 넣기
                if (inventoryLotResponse.status() == LotStatus.ACTIVE)
                    totalQuantity += inventoryLotResponse.quantity();
            }

            List<InventoryLotResponse> productLotResponses = entry.getValue();
            productSummaries.add(new ProductSummaryResponse(
                    id,
                    productBaseInfo.productName(),
                    productBaseInfo.price(),
                    productBaseInfo.minimumQuantity(),
                    totalQuantity,
                    productLotResponses));
        }

        return new ProductResponse(productSummaries);

    }

    public ProductSummaryResponse searchProduct(Long id) {
        // 유통기한 업데이트
        updateExpiredInventoryStatus();
        // 해당하는 인벤토리 가져오기
        List<Inventory> inventories = inventoryRepository.findByProductId(id);
        // 없으면 예외처리
        if (inventories.isEmpty()) {
            throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
        }
        // 해당하는 상품 최소 수량 가져오기(지금은 가맹점 하나로 치므로 1L로 고정)
        MinimumProduct minimumProducts = minimumProductRepository.findByProductIdAndStoreId(id, DEFAULT_STORE_ID)
                .orElseThrow(() -> new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND));
        List<InventoryLotResponse> productLotResponses = new ArrayList<>();
        int totalQuantity = 0;

        for (Inventory inventory : inventories) {
            if (inventory.getStatus() == LotStatus.ACTIVE) {
                totalQuantity += inventory.getQuantity();
            }
            productLotResponses.add(new InventoryLotResponse(
                    inventory.getProductId(),
                    inventory.getQuantity(),
                    inventory.getExpirationDate(),
                    inventory.getStatus(),
                    inventory.getProductCode()));
        }
        return new ProductSummaryResponse(
                inventories.get(0).getProductId(),
                inventories.get(0).getProductName(),
                inventories.get(0).getPrice(),
                minimumProducts.getMinimumQuantity(),
                totalQuantity,
                productLotResponses);
    }

    public List<ProductSummaryResponse> searchByName(String name) {
        // 유통기한 업데이트
        updateExpiredInventoryStatus();

        List<Inventory> inventories = inventoryRepository.findByProductNameContaining(name);
        // 없으면 예외처리
        if (inventories.isEmpty()) {
            throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
        }

        // productId별로 그룹화
        Map<Long, List<Inventory>> grouped = new HashMap<>();
        for (Inventory i : inventories) {
            grouped.computeIfAbsent(i.getProductId(), k -> new ArrayList<>()).add(i);
        }

        List<ProductSummaryResponse> results = new ArrayList<>();
        for (Map.Entry<Long, List<Inventory>> entry : grouped.entrySet()) {
            Long id = entry.getKey();
            List<Inventory> productInventories = entry.getValue();

            MinimumProduct minimumProducts = minimumProductRepository.findByProductIdAndStoreId(id, DEFAULT_STORE_ID)
                    .orElseThrow(() -> new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND));

            List<InventoryLotResponse> productLotResponses = new ArrayList<>();
            int totalQuantity = 0;

            for (Inventory inventory : productInventories) {
                if (inventory.getStatus() == LotStatus.ACTIVE) {
                    totalQuantity += inventory.getQuantity();
                }
                productLotResponses.add(new InventoryLotResponse(
                        inventory.getProductId(),
                        inventory.getQuantity(),
                        inventory.getExpirationDate(),
                        inventory.getStatus(),
                        inventory.getProductCode()));
            }

            results.add(new ProductSummaryResponse(
                    productInventories.get(0).getProductId(),
                    productInventories.get(0).getProductName(),
                    productInventories.get(0).getPrice(),
                    minimumProducts.getMinimumQuantity(),
                    totalQuantity,
                    productLotResponses));
        }
        return results;
    }

    public Void inbound(@Valid InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryRepository.findFirstByProductNameOrderByIdAsc(inventoryRequest.name())
                .orElseThrow(() -> new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND));

        Inventory insertInventory = Inventory.builder()
                .productId(inventory.getProductId())
                .quantity(inventoryRequest.quantity())
                .expirationDate(inventoryRequest.expirationDate())
                .status(LotStatus.ACTIVE)
                .productName(inventory.getProductName())
                .productCode(inventoryRequest.productCode())
                .price(inventory.getPrice())
                .description(inventory.getDescription())
                .build();
        inventoryRepository.save(insertInventory);
        return null;
    }

    public Void outbound(@Valid InventoryOutboundRequest request) {
        int count = request.quantity();
        LocalDate targetDate = LocalDate.now().plusMonths(request.monthsUntilExpiration());

        List<Inventory> inventories = inventoryRepository.findValidProductsWithLock(request.name(), targetDate);

        for (Inventory inventory : inventories) {
            int available = inventory.getQuantity();
            if (available >= count) {
                inventory.decreaseQuantity(count);
                count = 0;
                break;
            } else {
                inventory.decreaseQuantity(available);
                count -= available;
            }
        }

        // 재고 부족 시 예외 발생
        if (count > 0) {
            throw new BusinessException(InventoryErrorCode.OUT_OF_STOCK);
        }

        inventoryRepository.saveAll(inventories);

        return null;
    }

    public void updateExpiredInventoryStatus() {
        LocalDate today = LocalDate.now();

        // 만료된 재고만 DB에서 바로 가져오기
        List<Inventory> expiredInventories = inventoryRepository.findExpiredInventories(today);

        // 상태 변경
        for (Inventory inventory : expiredInventories) {
            inventory.markAsExpired(); // Inventory 엔티티에 상태 변경 메서드
        }

        // 한 번에 DB 저장
        inventoryRepository.saveAll(expiredInventories);
    }

}
