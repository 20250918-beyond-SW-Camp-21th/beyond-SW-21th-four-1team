//package com.spicy.backend.inventory.application;
//
//import com.spicy.backend.inventory.domain.Inventory;
//import com.spicy.backend.inventory.domain.LotStatus;
//import com.spicy.backend.inventory.domain.MinimumProduct;
//import com.spicy.backend.inventory.dto.request.InventoryOutboundRequest;
//import com.spicy.backend.inventory.dto.request.InventoryRequest;
//import com.spicy.backend.inventory.dto.response.InventoryLotResponse;
//import com.spicy.backend.inventory.dto.response.ProductBaseInfo;
//import com.spicy.backend.inventory.dto.response.ProductResponse;
//import com.spicy.backend.inventory.dto.response.ProductSummaryResponse;
//import com.spicy.backend.inventory.storage.InventoryRepository;
//import com.spicy.backend.inventory.storage.MinimumProductRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class InventoryServiceTests {
//        @Mock
//        private InventoryRepository inventoryRepository;
//
//        @Mock
//        private MinimumProductRepository minimumProductRepository;
//        @Spy
//        @InjectMocks
//        private InventoryService inventoryService;
//
//        @Test
//        @DisplayName("모든 상품 정상 검색 테스트")
//        void getAllProduct() {
//                Long id = 1L;
//
//                // given
//                InventoryLotResponse lot1 = new InventoryLotResponse(
//                                1L,
//                                5,
//                                LocalDate.now().plusDays(10),
//                                LotStatus.ACTIVE,
//                                "CODE-1");
//
//                InventoryLotResponse lot2 = new InventoryLotResponse(
//                                1L,
//                                3,
//                                LocalDate.now().plusDays(20),
//                                LotStatus.ACTIVE,
//                                "CODE-2");
//
//                MinimumProduct minimumProduct = MinimumProduct.builder()
//                                .productId(id)
//                                .storeId(1L)
//                                .minimumQuantity(3)
//                                .build();
//
//                ProductBaseInfo baseInfo = new ProductBaseInfo(
//                                1L,
//                                "사이다",
//                                BigDecimal.valueOf(1200),
//                                3);
//
//                doNothing().when(inventoryService).updateExpiredInventoryStatus();
//                when(inventoryRepository.findAllProduct())
//                                .thenReturn(List.of(lot1, lot2));
//                when(inventoryRepository.findAllProductBaseInfo())
//                                .thenReturn(List.of(baseInfo));
//                // when
//                ProductResponse response = inventoryService.getAllProduct();
//
//                // then
//                assertThat(response.products()).hasSize(1);
//                ProductSummaryResponse summary = response.products().get(0);
//
//                assertEquals("사이다", summary.productName());
//                assertEquals(8, summary.totalQuantity());
//                assertThat(summary.products()).hasSize(2);
//
//        }
//
//        @Test
//        @DisplayName("아이디로 상품 정상 검색 테스트")
//        void searchProduct() {
//                // given
//                Long id = 1L;
//                Inventory activeInventory = Inventory.builder()
//                                .productId(id)
//                                .quantity(5)
//                                .expirationDate(LocalDate.now().plusDays(10))
//                                .status(LotStatus.ACTIVE)
//                                .productName("사이다")
//                                .productCode("CID-1")
//                                .price(BigDecimal.valueOf(1200))
//                                .description("음료")
//                                .build();
//
//                Inventory expiredInventory = Inventory.builder()
//                                .productId(id)
//                                .quantity(10)
//                                .expirationDate(LocalDate.now().minusDays(1))
//                                .status(LotStatus.EXPIRED)
//                                .productName("사이다")
//                                .productCode("CID-OLD")
//                                .price(BigDecimal.valueOf(1200))
//                                .description("음료")
//                                .build();
//
//                MinimumProduct minimumProduct = MinimumProduct.builder()
//                                .productId(id)
//                                .storeId(1L)
//                                .minimumQuantity(3)
//                                .build();
//
//                when(inventoryRepository.findByProductId(id))
//                                .thenReturn(List.of(activeInventory, expiredInventory));
//
//                when(minimumProductRepository.findByProductIdAndStoreId(id, 1L))
//                                .thenReturn(Optional.of(minimumProduct));
//                doNothing().when(inventoryService).updateExpiredInventoryStatus();
//
//                // when
//                ProductSummaryResponse result = inventoryService.searchProduct(id);
//
//                // then
//                assertEquals(15, result.totalQuantity());
//                assertEquals(3, result.minimumQuantity());
//        }
//
//        @Test
//        @DisplayName("이름으로 정상 상품 검색")
//        void searchByName() {
//                // given
//                String name = "사이다";
//                Inventory activeInventory = Inventory.builder()
//                                .productId(1L)
//                                .quantity(5)
//                                .expirationDate(LocalDate.now().plusDays(10))
//                                .status(LotStatus.ACTIVE)
//                                .productName("사이다")
//                                .productCode("CID-1")
//                                .price(BigDecimal.valueOf(1200))
//                                .description("음료")
//                                .build();
//
//                Inventory expiredInventory = Inventory.builder()
//                                .productId(1L)
//                                .quantity(10)
//                                .expirationDate(LocalDate.now().minusDays(1))
//                                .status(LotStatus.EXPIRED)
//                                .productName("사이다")
//                                .productCode("CID-OLD")
//                                .price(BigDecimal.valueOf(1200))
//                                .description("음료")
//                                .build();
//
//                MinimumProduct minimumProduct = MinimumProduct.builder()
//                                .productId(1L)
//                                .storeId(1L)
//                                .minimumQuantity(3)
//                                .build();
//                when(inventoryRepository.findByProductNameContaining(name))
//                                .thenReturn(List.of(activeInventory, expiredInventory));
//
//                when(minimumProductRepository.findByProductIdAndStoreId(1L, 1L))
//                                .thenReturn(Optional.of(minimumProduct));
//
//                // when
//                List<ProductSummaryResponse> results = inventoryService.searchByName(name);
//
//                // then
//                assertEquals(1, results.size());
//                ProductSummaryResponse result = results.get(0);
//                assertEquals(5, result.totalQuantity()); // Now only counts ACTIVE
//                assertEquals(3, result.minimumQuantity());
//        }
//
//        @Test
//        @DisplayName("정상적으로 입고")
//        void inbound() {
//                // given
//                InventoryRequest request = new InventoryRequest(
//                                "productName",
//                                5,
//                                LocalDate.now().plusDays(30),
//                                "CID-1");
//
//                Inventory activeInventory = Inventory.builder()
//                                .productId(1L)
//                                .quantity(5)
//                                .expirationDate(LocalDate.now().plusDays(10))
//                                .status(LotStatus.ACTIVE)
//                                .productName("사이다")
//                                .productCode("CID-2")
//                                .price(BigDecimal.valueOf(1200))
//                                .description("음료")
//                                .build();
//
//                when(inventoryRepository.findFirstByProductNameOrderByIdAsc(request.name()))
//                                .thenReturn(Optional.of(activeInventory));
//                // when
//                inventoryService.inbound(request);
//                // then
//                verify(inventoryRepository, times(1)).save(any());
//        }
//
//        @Test
//        @DisplayName("재고 값이 떨어지는지 정상 확인")
//        void outbound() {
//                // given
//                InventoryOutboundRequest request = new InventoryOutboundRequest("사이다", 2, 2);
//                Inventory activeInventory = Inventory.builder()
//                                .productId(1L)
//                                .quantity(5)
//                                .expirationDate(LocalDate.now().plusDays(10))
//                                .status(LotStatus.ACTIVE)
//                                .productName("사이다")
//                                .productCode("CID-2")
//                                .price(BigDecimal.valueOf(1200))
//                                .description("음료")
//                                .build();
//                when(inventoryRepository.findValidProductsWithLock(
//                                eq(request.name()),
//                                any(LocalDate.class))).thenReturn(List.of(activeInventory));
//                // when
//                inventoryService.outbound(request);
//                // then
//                ArgumentCaptor<List<Inventory>> captor = ArgumentCaptor.forClass(List.class);
//                verify(inventoryRepository, times(1)).saveAll(captor.capture());
//
//                List<Inventory> saved = captor.getValue();
//                // 출고 후 재고 수량이 감소했는지 검증
//                assertEquals(3, saved.get(0).getQuantity());
//        }
//
//}
