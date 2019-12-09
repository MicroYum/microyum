package com.microyum.controller;

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

    private StockService stockService;

    @Autowired
    private InvestmentStrategySchedule investmentStrategySchedule;

    @RequestMapping(value = "/public/schedule/refer/stock")
    public void referStockData() {
        referStockDataSchedule.getRealtimeStockBySina();
    }

    @RequestMapping(value = "/public/schedule/stock/value")
    public void stockValueInterval() {
        investmentStrategySchedule.valueInterval();
    }

    public void repairStockData() {stockService.repairStockData();}
}
