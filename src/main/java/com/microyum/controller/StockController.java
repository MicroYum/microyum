package com.microyum.controller;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.PinYinUtils;
import com.microyum.dto.CalculateStockTransactionCostDto;
import com.microyum.dto.StockBaseDto;
import com.microyum.model.stock.MyStockBase;
import com.microyum.service.RepairService;
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
    @Autowired
    private RepairService repairService;

    /**
     * 获取股票的基本信息
     *
     * @param area
     * @param code
     * @param request
     * @return
     */
    @RequestMapping(value = "/public/stock/{area}/{code}/detail", produces = "application/json")
    public BaseResponseDTO referStockDetail(@PathVariable("area") String area, @PathVariable("code") String code, HttpServletRequest request) {
        return stockService.referStockDetail(area, code, request.getParameter("start"), request.getParameter("end"));
    }

    @RequestMapping(value = "/public/stock/base/list", produces = "application/json")
    public BaseResponseDTO referStockList(int page, int limit, String stock, Long timestamp) {
        return stockService.referStockList(page, limit, stock);
    }

    @RequestMapping(value = "/public/stock/data/latest", produces = "application/json")
    public BaseResponseDTO referStockTradeDayDetail(String area, String stockCode) {

        return stockService.referStockTradeDayDetail(area, stockCode);
    }

    @RequestMapping(value = "/public/stock/transaction/cost", produces = "application/json")
    public BaseResponseDTO calculateStockTransactionCost(CalculateStockTransactionCostDto dto) {
        return stockService.calculateStockTransactionCost(dto);
    }

    @RequestMapping(value = "/stock/base/save", produces = "application/json")
    public BaseResponseDTO saveStockBase(StockBaseDto stockBase) {
        return stockService.saveStockBase(stockBase);
    }

    @RequestMapping(value = "/stock/base/{id}", produces = "application/json")
    public BaseResponseDTO referStockBase(@PathVariable("id") Long id) {
        return stockService.referStockBase(id);
    }

    @RequestMapping(value = "/stock/base/update", produces = "application/json")
    public BaseResponseDTO updateStockBase(MyStockBase stockBase) {
        return stockService.updateStockBase(stockBase);
    }

    @RequestMapping(value = "/stock/base/{id}/delete", produces = "application/json")
    public BaseResponseDTO deleteStockBase(@PathVariable("id") Long id) {

        return stockService.deleteStockBase(id);
    }

    @RequestMapping(value = "/stock/base/{area}/{code}/exist", produces = "application/json")
    public BaseResponseDTO checkStockExist(@PathVariable("area") String area, @PathVariable("code") String code) {
        return stockService.checkStockExist(area, code);
    }

    @RequestMapping(value = "/refer/entity/list", produces = "application/json")
    public BaseResponseDTO referEntityList(Integer type) {
        return stockService.referEntityList(type);
    }

    @RequestMapping(value = "/public/makeup/strategy", produces = "application/json")
    public BaseResponseDTO makeupStrategy(String date) {
        return stockService.makeupStrategy(date);
    }

    @RequestMapping(value = "/public/hanzi/initials", produces = "application/json")
    public BaseResponseDTO getHanziInitials(String hanzi) {
        return new BaseResponseDTO(HttpStatus.OK, PinYinUtils.getHanziInitials(hanzi));
    }

    @RequestMapping(value = "/public/repair/chg/percent", produces = "application/json")
    public BaseResponseDTO repairChgAndPercent() {
        repairService.repairChgAndPercent();
        return new BaseResponseDTO(HttpStatus.OK);
    }

    @RequestMapping(value = "/public/remove/repeat/strategy", produces = "application/json")
    public BaseResponseDTO removeRepeatStrategyData() {
        return  stockService.removeRepeatStrategyData();
    }
}
