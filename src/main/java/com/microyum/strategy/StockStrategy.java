package com.microyum.strategy;

import com.google.common.collect.Maps;
import com.microyum.common.enums.StockStrategyEnum;
import com.microyum.dao.jdbc.MyStockJdbcDao;
import com.microyum.dto.StockLatestDataDto;
import com.microyum.model.stock.MyStockBase;
import com.microyum.model.stock.MyStockDailyStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class StockStrategy {

    @Autowired
    private MyStockJdbcDao stockJdbcDao;

    public MyStockDailyStrategy calcStockValueRange(MyStockBase stockBase) {

        Integer tradeCount = stockJdbcDao.countStockDataByCode(stockBase.getStockCode(), null);

        MyStockDailyStrategy dailyStrategy = new MyStockDailyStrategy();
        dailyStrategy.setStockCode(stockBase.getStockCode());
        dailyStrategy.setTradeDate(new Date());

        // 获取所有后复权数据的最高点记录、最低点记录
        BigDecimal highest = stockJdbcDao.getHighestStock(stockBase.getStockCode());
        BigDecimal lowest = stockJdbcDao.getLowestStock(stockBase.getStockCode());

        // 将数据分为四档：低估5%, 低档15%, 中档60%, 高档15%, 高估5%
        BigDecimal interval = highest.subtract(lowest).divide(BigDecimal.valueOf(20));

        // 获取最新的标的数据
        StockLatestDataDto latestStock = stockJdbcDao.referLatestStockData(stockBase.getStockCode());

        dailyStrategy.setLatestPrice(latestStock.getClose());
        dailyStrategy.setLatestHfqPrice(latestStock.getHfqClose());

        dailyStrategy.setBuyingMin(lowest);
        dailyStrategy.setBuyingMax(interval.add(lowest));
        dailyStrategy.setUnderMin(interval.add(lowest));
        dailyStrategy.setUnderMax(interval.multiply(BigDecimal.valueOf(4)).add(lowest));
        dailyStrategy.setMiddleMin(interval.multiply(BigDecimal.valueOf(4)).add(lowest));
        dailyStrategy.setMiddleMax(interval.multiply(BigDecimal.valueOf(15)).add(lowest));
        dailyStrategy.setOverMin(interval.multiply(BigDecimal.valueOf(15)).add(lowest));
        dailyStrategy.setOverMax(highest.subtract(interval));
        dailyStrategy.setSellingMin(highest.subtract(interval));
        dailyStrategy.setSellingMax(highest);
        dailyStrategy.setTradeCount(tradeCount);

        DecimalFormat df = new DecimalFormat("0.00");

        Map<String, BigDecimal> lowPrice = Maps.newHashMap();
        lowPrice.put("lowPrice", latestStock.getHfqClose());
        Integer lowPriceDays = stockJdbcDao.countStockDataByCode(stockBase.getStockCode(), lowPrice);
        dailyStrategy.setPriceRate(new BigDecimal(df.format((float) lowPriceDays / dailyStrategy.getTradeCount())));

        Map<String, BigDecimal> lowVolume = Maps.newHashMap();
        lowVolume.put("lowVolume", latestStock.getTradeCount());
        Integer lowVolumeDays = stockJdbcDao.countStockDataByCode(stockBase.getStockCode(), lowVolume);
        dailyStrategy.setVolumeRate(new BigDecimal(df.format((float) lowVolumeDays / dailyStrategy.getTradeCount())));

        // 小于500个交易日不做建议判断
        if (tradeCount.intValue() > dailyStrategy.getTradeCount()) {
            dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_NO_ADVICE.getCode());
        } else {
            if (dailyStrategy.getBuyingMax().compareTo(latestStock.getHfqClose()) == 1) {
                if (dailyStrategy.getTradeCount().doubleValue() * 0.05 > dailyStrategy.getVolumeRate().doubleValue()) {
                    dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_BUYING.getCode());
                } else if (dailyStrategy.getTradeCount().doubleValue() * 0.4 < dailyStrategy.getVolumeRate().doubleValue()) {
                    dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_BUYING.getCode());
                } else {
                    dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_UNDER_VALUE.getCode());
                }
            } else if (dailyStrategy.getUnderMax().compareTo(latestStock.getHfqClose()) == 1) {
                dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_UNDER_VALUE.getCode());
            } else if (dailyStrategy.getMiddleMax().compareTo(latestStock.getHfqClose()) == 1) {
                dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_MIDDLE_VALUE.getCode());
            } else if (dailyStrategy.getOverMax().compareTo(latestStock.getHfqClose()) == 1) {
                dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_OVER_VALUE.getCode());
            } else {
                dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_SELLING.getCode());
            }
        }

        return dailyStrategy;
    }
}
