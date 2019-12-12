package com.microyum.controller;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.CalculateStockTransactionCostDto;
import com.microyum.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    /**
     * 获取股票的基本信息
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/public/stock/{id}/detail", produces = "application/json")
    public BaseResponseDTO referStockDetail(@PathVariable("id") String id, HttpServletRequest request) {

        return stockService.referStockDetail(id, request.getParameter("start"), request.getParameter("end"));
    }

    @RequestMapping(value = "/public/stock/list", produces = "application/json")
    public BaseResponseDTO referStockList(int page, int limit, String stock) {

        return stockService.referStockList(page, limit, stock);
    }

    @RequestMapping(value = "/public/stock/data/latest", produces = "application/json")
    public BaseResponseDTO referStockTradeDayDetail(String stockCode) {

        return stockService.referStockTradeDayDetail(stockCode);
    }

    @RequestMapping(value = "/public/stock/transaction/cost", produces = "application/json")
    public BaseResponseDTO calculateStockTransactionCost(CalculateStockTransactionCostDto dto) {

        return stockService.calculateStockTransactionCost(dto);
    }

}
