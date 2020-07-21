package com.microyum.controller;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.schedule.InvestmentStrategySchedule;
import com.microyum.schedule.ReferStockDataSchedule;
import com.microyum.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {

    @Autowired
    private ReferStockDataSchedule referStockDataSchedule;

    @Autowired
    private StockService stockService;

    @Autowired
    private InvestmentStrategySchedule investmentStrategySchedule;

    @RequestMapping(value = "/public/schedule/refer/stock")
    public BaseResponseDTO referStockData() {
        referStockDataSchedule.getRealtimeStockBySina();
        return new BaseResponseDTO(HttpStatus.OK);
    }

    @RequestMapping(value = "/public/repair/stock/data")
    public BaseResponseDTO repairStockData() {
        stockService.repairStockData(null, null);
        return new BaseResponseDTO(HttpStatus.OK);
    }

    @RequestMapping(value = "/public/repair/hfq/data")
    public BaseResponseDTO repairHfqData() {
        referStockDataSchedule.repairHfqData();
        return new BaseResponseDTO(HttpStatus.OK);
    }

    @RequestMapping(value = "/public/schedule/value/range")
    public BaseResponseDTO calcValueRange() {
        investmentStrategySchedule.calcValueRange();
        return new BaseResponseDTO(HttpStatus.OK);
    }

    @RequestMapping(value = "/public/schedule/mail/buying/stock")
    public BaseResponseDTO mailBuyingStock() {
        investmentStrategySchedule.mailBuyingStock();
        return new BaseResponseDTO(HttpStatus.OK);
    }

    @RequestMapping(value = "/public/schedule/personal/value/interval")
    public BaseResponseDTO personalValueInterval() {
        investmentStrategySchedule.personalValueInterval();
        return new BaseResponseDTO(HttpStatus.OK);
    }

    @RequestMapping(value = "/public/schedule/turnover/rate")
    public BaseResponseDTO calcTurnoverRate() {
        investmentStrategySchedule.calcTurnoverRate();
        return new BaseResponseDTO(HttpStatus.OK);
    }

    @RequestMapping(value = "/public/schedule/init/turnover/rate")
    public BaseResponseDTO initTurnoverRate() {
        investmentStrategySchedule.initTurnoverRate();
        return new BaseResponseDTO(HttpStatus.OK);
    }
}
