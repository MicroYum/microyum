package com.microyum.service.impl;

import com.microyum.common.Constants;
import com.microyum.common.util.DateUtils;
import com.microyum.dao.jpa.MyStockBaseDao;
import com.microyum.dao.jpa.MyStockDailyStrategyDao;
import com.microyum.model.stock.MyStockBase;
import com.microyum.model.stock.MyStockDailyStrategy;
import com.microyum.service.RepairService;
import com.microyum.strategy.StockStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RepairServiceImpl implements RepairService {

    @Autowired
    private MyStockBaseDao stockBaseDao;
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

            log.info(DateUtils.formatDate(calDate, DateUtils.DATE_FORMAT) + " start ...");

            if (!stockStrategy.isTradingDay(calDate)) {
                calDate = DateUtils.addDays(calDate, 1);
                continue;
            }

            MyStockDailyStrategy dailyStrategy = stockStrategy.calcStockValueRangeByDate(area, stockCode, calDate);
            if (dailyStrategy != null) {
                dailyStrategyDao.save(dailyStrategy);
            }

            log.info(DateUtils.formatDate(calDate, DateUtils.DATE_FORMAT) + " end .");

            calDate = DateUtils.addDays(calDate, 1);
        }
    }
}
