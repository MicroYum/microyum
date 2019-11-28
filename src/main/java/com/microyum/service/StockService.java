package com.microyum.service;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.CalculateStockTransactionCostDTO;

public interface StockService {

    BaseResponseDTO referStockList(int pageNo, int pageSize, String stock);

    BaseResponseDTO referStockDetail(String stockCode, String startDate, String endDate);

    BaseResponseDTO referStockTradeDayDetail(String stockCode);

    BaseResponseDTO calculateStockTransactionCost(CalculateStockTransactionCostDTO dto);
}
