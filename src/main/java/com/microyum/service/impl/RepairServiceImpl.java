package com.microyum.service.impl;

import com.microyum.common.Constants;
import com.microyum.common.util.DateUtils;
import com.microyum.dao.jpa.MyStockBaseDao;
import com.microyum.dao.jpa.MyStockDailyStrategyDao;
import com.microyum.dao.jpa.MyStockDataDao;
import com.microyum.model.stock.MyStockBase;
import com.microyum.model.stock.MyStockDailyStrategy;
import com.microyum.model.stock.MyStockData;
import com.microyum.service.RepairService;
import com.microyum.strategy.StockStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RepairServiceImpl implements RepairService {

    @Autowired
    private MyStockBaseDao stockBaseDao;
    @Autowired
    private MyStockDataDao stockDataDao;
    @Autowired
    private StockStrategy stockStrategy;
    @Autowired
    private MyStockDailyStrategyDao dailyStrategyDao;

    @Override
    public void repairStrategyData(String area, String stockCode) {

        // 默认的策略数据开始日为：2019-01-01
        Date calDate = DateUtils.parseDate(Constants.STRATEGY_DEFAULT_DATE, DateUtils.DATE_FORMAT);

        // 找出股票的上市时间
        MyStockBase stockBase = stockBaseDao.findByAreaAndStockCode(area, stockCode);

        // 如果股票的上市时间 > 默认策略开始日，则策略开始日 = 上市时间 + 1年
        if (DateUtils.compare(calDate, stockBase.getListingDate()) == -1) {
            calDate = DateUtils.addDays(calDate, 365);
        }

        if (DateUtils.compare(calDate, new Date()) == 1) {
            return;
        }

        // 调用StockStrategy类的calcStockValueRangeByDate方法，开始补齐策略
        while (!DateUtils.isSameDay(calDate, new Date())) {

            log.debug(DateUtils.formatDate(calDate, DateUtils.DATE_FORMAT) + " start ...");

            if (!stockStrategy.isTradingDay(calDate)) {
                calDate = DateUtils.addDays(calDate, 1);
                continue;
            }

            MyStockDailyStrategy dailyStrategy = stockStrategy.calcStockValueRangeByDate(area, stockCode, calDate);
            if (dailyStrategy != null) {
                dailyStrategyDao.save(dailyStrategy);
            }

            log.debug(DateUtils.formatDate(calDate, DateUtils.DATE_FORMAT) + " end .");

            calDate = DateUtils.addDays(calDate, 1);
        }
    }

    @Override
    public void repairChgAndPercent() {

        List<MyStockBase> stockBases = stockBaseDao.findAll();
        for (MyStockBase stockBase : stockBases) {

            log.info("current stock = " + stockBase.getArea() + stockBase.getStockCode() + " starting ...");

            List<MyStockData> stockDatas = stockDataDao.findByAreaAndStockCode(stockBase.getArea(), stockBase.getStockCode());
            boolean isFirst = true;
            BigDecimal preClose = null;
            for (MyStockData stockData : stockDatas) {
                BigDecimal chg, percent;

                if (isFirst) {
                    chg = stockData.getClose().subtract(stockData.getOpen());
                    percent = chg.multiply(new BigDecimal(100)).divide(stockData.getOpen(), 4);
                    isFirst = false;
                } else {
                    chg = stockData.getClose().subtract(preClose);
                    percent = chg.multiply(new BigDecimal(100)).divide(stockData.getOpen(), 4);
                }

                stockData.setChg(chg);
                stockData.setPercent(percent);
                stockDataDao.save(stockData);
                preClose = stockData.getClose();
            }

            log.info("current stock = " + stockBase.getArea() + stockBase.getStockCode() + " end");
        }
    }
}
