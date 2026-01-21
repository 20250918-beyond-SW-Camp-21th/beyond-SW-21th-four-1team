package com.spicy.backend.settlement.application;

import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.order.dao.order.OrderItemRepository;
import com.spicy.backend.order.dao.order.OrderRepository;
import com.spicy.backend.settlement.dao.SettlementRepository;
import com.spicy.backend.settlement.domain.Settlement;
import com.spicy.backend.settlement.dto.request.DailySettlementRequest;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import com.spicy.backend.settlement.error.SettlementErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTests {

    @InjectMocks
    private SettlementService settlementService;

    @Mock
    private SettlementRepository settlementRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private SettlementFileService settlementFileService;

    @Test
    @DisplayName("일별 정산 조회 - 성공")
    void getDailySettlement_Success() {
        // Given
        Long storeId = 1L;
        LocalDate date = LocalDate.of(2026, 1, 21);
        DailySettlementRequest request = new DailySettlementRequest(storeId, date);

        Settlement mockSettlement = Settlement.builder()
                .orderCount(5)
                .totalSettlementAmount(new BigDecimal("100000"))
                .build();

        // 레포지토리 동작
        given(settlementRepository.findByStoreIdAndSettlementDate(storeId, date))
                .willReturn(Optional.of(mockSettlement));
        given(settlementRepository.findByStoreIdAndSettlementDateBetween(any(), any(), any()))
                .willReturn(List.of(mockSettlement));

        // When
        DailySettlementResponse response = settlementService.getDailySettlement(request);

        // Then
        assertThat(response.orderCount()).isEqualTo(5);
        assertThat(response.dailyAmount()).isEqualByComparingTo("100000");
        verify(settlementRepository).findByStoreIdAndSettlementDate(storeId, date);
    }

    @Test
    @DisplayName("일별 정산 조회 - 실패 - 내역 없음 ")
    void getDailySettlement_NotFound() {
        // Given 내역이 없는 상황을 가정
        DailySettlementRequest request = new DailySettlementRequest(1L, LocalDate.now());
        given(settlementRepository.findByStoreIdAndSettlementDate(any(), any()))
                .willReturn(Optional.empty());

        // When 예외 발생
        org.assertj.core.api.ThrowableAssert.ThrowingCallable action =
                () -> settlementService.getDailySettlement(request);

        // Then 의도한 예외와 에러 코드가 발생하는지
        assertThatThrownBy(action)
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SettlementErrorCode.SETTLEMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("정산 생성 - 실패 - 이미 존재")
    void createSettlement_AlreadyExists() {
        // Given 이미 정산 데이터가 존재하는 상황 준비
        Long storeId = 1L;
        LocalDate date = LocalDate.now();
        given(settlementRepository.findByStoreIdAndSettlementDate(storeId, date))
                .willReturn(Optional.of(Settlement.builder().build()));

        // When 중복 생성을 시도하는 행위 정의
        org.assertj.core.api.ThrowableAssert.ThrowingCallable action =
                () -> settlementService.createSettlement(storeId, date);

        // Then 중복 예외가 발생하는지 검증
        assertThatThrownBy(action)
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SettlementErrorCode.SETTLEMENT_ALREADY_EXISTS);
    }
}