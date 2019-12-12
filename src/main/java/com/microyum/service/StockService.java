package com.microyum.service;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.CalculateStockTransactionCostDto;

public interface StockService {

    BaseResponseDTO referStockList(int pageNo, int pageSize, String stock);

    BaseResponseDTO referStockDetail(String stockCode, String startDate, String endDate);

    BaseResponseDTO referStockTradeDayDetail(String stockCode);

    BaseResponseDTO calculateStockTransactionCost(CalculateStockTransactionCostDto dto);

    void repairStockData();
}
