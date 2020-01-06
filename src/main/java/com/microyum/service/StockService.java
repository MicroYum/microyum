package com.microyum.service;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.CalculateStockTransactionCostDto;
import com.microyum.dto.StockBaseDto;
import com.microyum.model.stock.MyStockBase;

public interface StockService {

    BaseResponseDTO referStockList(int pageNo, int pageSize, String stock);

    BaseResponseDTO referStockDetail(String area, String stockCode, String startDate, String endDate);

    BaseResponseDTO referStockTradeDayDetail(String area, String stockCode);

    BaseResponseDTO calculateStockTransactionCost(CalculateStockTransactionCostDto dto);

    void repairStockData(String area, String stockCode);

    BaseResponseDTO referStockBase(Long id);
    BaseResponseDTO saveStockBase(StockBaseDto stockBase);
    BaseResponseDTO updateStockBase(MyStockBase stockBase);
    BaseResponseDTO deleteStockBase(Long id);

    BaseResponseDTO checkStockExist(String code);
}
