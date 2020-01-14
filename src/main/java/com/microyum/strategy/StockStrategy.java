package com.microyum.strategy;

import com.google.common.collect.Maps;
import com.microyum.common.enums.StockStrategyEnum;
import com.microyum.common.util.DateUtils;
import com.microyum.dao.jdbc.MyStockJdbcDao;
import com.microyum.dao.jpa.MyDayOffDao;
import com.microyum.dto.StockLatestDataDto;
import com.microyum.model.common.MyDayOff;
import com.microyum.model.stock.MyStockBase;
import com.microyum.model.stock.MyStockDailyStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class StockStrategy {

    @Autowired
    private MyStockJdbcDao stockJdbcDao;
    @Autowired
    private MyDayOffDao dayOffDao;

    public MyStockDailyStrategy calcStockValueRange(MyStockBase stockBase) {

        // 判断交易天数小于200天(1年左右)，则不做策略判断
        Integer tradeCount = stockJdbcDao.countStockDataByCode(stockBase.getArea(), stockBase.getStockCode(), null);
        if (tradeCount < 200) {
            return null;
        }

        MyStockDailyStrategy dailyStrategy = new MyStockDailyStrategy();
        dailyStrategy.setArea(stockBase.getArea());
        dailyStrategy.setStockCode(stockBase.getStockCode());
        dailyStrategy.setTradeDate(new Date());

        // 获取所有后复权数据的最高点记录、最低点记录
        BigDecimal highest = stockJdbcDao.getHighestStock(stockBase.getArea(), stockBase.getStockCode());
        BigDecimal lowest = stockJdbcDao.getLowestStock(stockBase.getArea(), stockBase.getStockCode());

        // 将数据分为四档：低估5%, 低档15%, 中档60%, 高档15%, 高估5%
        BigDecimal interval = highest.subtract(lowest).divide(BigDecimal.valueOf(20));

        // 获取最新的标的数据
        StockLatestDataDto latestStock = stockJdbcDao.referLatestStockData(stockBase.getArea(), stockBase.getStockCode());

        // 指定的日期取值为null，说明这一天可能停牌，不补齐策略数据
        if (latestStock == null) {
            return null;
        }

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
        Integer lowPriceDays = stockJdbcDao.countStockDataByCode(stockBase.getArea(), stockBase.getStockCode(), lowPrice);
        dailyStrategy.setPriceRate(new BigDecimal(df.format((float) lowPriceDays / dailyStrategy.getTradeCount())));

        Map<String, BigDecimal> lowVolume = Maps.newHashMap();
        lowVolume.put("lowVolume", latestStock.getTradeCount());
        Integer lowVolumeDays = stockJdbcDao.countStockDataByCode(stockBase.getArea(), stockBase.getStockCode(), lowVolume);
        dailyStrategy.setVolumeRate(new BigDecimal(df.format((float) lowVolumeDays / dailyStrategy.getTradeCount())));

        if (dailyStrategy.getBuyingMax().compareTo(latestStock.getHfqClose()) == 1) {
            dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_BUYING.getCode());
        } else if (dailyStrategy.getUnderMax().compareTo(latestStock.getHfqClose()) == 1) {
            dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_UNDER_VALUE.getCode());
        } else if (dailyStrategy.getMiddleMax().compareTo(latestStock.getHfqClose()) == 1) {
            dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_MIDDLE_VALUE.getCode());
        } else if (dailyStrategy.getOverMax().compareTo(latestStock.getHfqClose()) == 1) {
            dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_OVER_VALUE.getCode());
        } else {
            dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_SELLING.getCode());
        }

        return dailyStrategy;
    }

    public MyStockDailyStrategy calcStockValueRangeByDate(String area, String stockCode, Date date) {

        Integer tradeCount = stockJdbcDao.countStockDataByCode(area, stockCode, date, null);
        if (tradeCount < 200) {
            return null;
        }

        MyStockDailyStrategy dailyStrategy = new MyStockDailyStrategy();
        dailyStrategy.setArea(area);
        dailyStrategy.setStockCode(stockCode);
        dailyStrategy.setTradeDate(date);

        // 获取所有后复权数据的最高点记录、最低点记录
        BigDecimal highest = stockJdbcDao.getHighestStock(area, stockCode, date);
        BigDecimal lowest = stockJdbcDao.getLowestStock(area, stockCode, date);

        // 将数据分为四档：低估5%, 低档15%, 中档60%, 高档15%, 高估5%
        BigDecimal interval = highest.subtract(lowest).divide(BigDecimal.valueOf(20));

        // 获取最新的标的数据
        StockLatestDataDto latestStock = stockJdbcDao.referLatestStockData(area, stockCode, date);

        // 指定的日期取值为null，说明这一天可能停牌，不补齐策略数据
        if (latestStock == null) {
            return null;
        }

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
        Integer lowPriceDays = stockJdbcDao.countStockDataByCode(area, stockCode, date, lowPrice);
        dailyStrategy.setPriceRate(new BigDecimal(df.format((float) lowPriceDays / dailyStrategy.getTradeCount())));

        Map<String, BigDecimal> lowVolume = Maps.newHashMap();
        lowVolume.put("lowVolume", latestStock.getTradeCount());
        Integer lowVolumeDays = stockJdbcDao.countStockDataByCode(area, stockCode, date, lowVolume);
        dailyStrategy.setVolumeRate(new BigDecimal(df.format((float) lowVolumeDays / dailyStrategy.getTradeCount())));

        if (dailyStrategy.getBuyingMax().compareTo(latestStock.getHfqClose()) == 1) {
            dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_BUYING.getCode());
        } else if (dailyStrategy.getUnderMax().compareTo(latestStock.getHfqClose()) == 1) {
            dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_UNDER_VALUE.getCode());
        } else if (dailyStrategy.getMiddleMax().compareTo(latestStock.getHfqClose()) == 1) {
            dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_MIDDLE_VALUE.getCode());
        } else if (dailyStrategy.getOverMax().compareTo(latestStock.getHfqClose()) == 1) {
            dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_OVER_VALUE.getCode());
        } else {
            dailyStrategy.setStrategy(StockStrategyEnum.STRATEGY_SELLING.getCode());
        }

        return dailyStrategy;
    }

    public boolean isTradingDay() {
        return this.isTradingDay(null);
    }

    public boolean isTradingDay(Date date) {

        if (date == null) {
            date = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayWeek == 1 || dayWeek == 7) {
            log.info(DateUtils.formatDate(date, DateUtils.DATE_FORMAT) + ", 周六、周日.");
            return false;
        }

        MyDayOff dayOff = dayOffDao.findByTradeDate(DateUtils.formatDate(date, DateUtils.DATE_FORMAT));
        if (dayOff != null) {
            log.info(DateUtils.formatDate(date, DateUtils.DATE_FORMAT) + ", 国定假日.");
            return false;
        }

        return true;
    }
}
