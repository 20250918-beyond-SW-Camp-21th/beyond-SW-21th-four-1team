package com.spicy.backend.demandplan.service;

import com.spicy.backend.demandplan.controller.dto.response.StockResponseDto;

import java.util.List;

public interface ExternalDataFetcher {

    StockResponseDto getStock(Long productId);
    Integer getRecentOrderCount(Long productId, int month);
}
