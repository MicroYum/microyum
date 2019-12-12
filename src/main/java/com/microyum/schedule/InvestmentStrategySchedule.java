package com.microyum.schedule;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.microyum.bo.StockStrategyBO;
import com.microyum.common.util.DateUtils;
import com.microyum.common.util.MailUtils;
import com.microyum.dao.MyStockDao;
import com.microyum.dto.StockLatestDataDto;
import com.microyum.model.MyStockBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 投资策略定时任务
 */
@Component
@Slf4j
public class InvestmentStrategySchedule {

    @Autowired
    private MyStockDao stockDao;

    // 进入买卖区间，每天16:30执行
    @Scheduled(cron = "0 30 16 * * ? ")
    public void valueInterval() {

        log.info("买卖区判断定时任务开始...");
        List<StockStrategyBO> strategyBOList = Lists.newArrayList();

        // 获取所有股票的列表
        List<MyStockBase> listStock = stockDao.getObservedListNotIndex();

        for (MyStockBase stockBase : listStock) {
            // 获取所有后复权数据的最高点记录、最低点记录
            BigDecimal highest = stockDao.getHighestStock(stockBase.getStockCode());
            BigDecimal lowest = stockDao.getLowestStock(stockBase.getStockCode());


            // 将数据分为四档：低估5%, 低档15%, 中档60%, 高档15%, 高估5%
            BigDecimal interval = highest.subtract(lowest).divide(BigDecimal.valueOf(20));

            StockLatestDataDto latestStock = stockDao.referLatestStockData(stockBase.getStockCode());

            if (interval.multiply(BigDecimal.valueOf(4)).add(lowest).compareTo(latestStock.getHfqClose()) == 1) {

                StockStrategyBO strategyBO = new StockStrategyBO();
                strategyBO.setStockCode(stockBase.getStockCode());
                strategyBO.setStockName(stockBase.getStockName());
                strategyBO.setCurrent(latestStock.getClose());
                strategyBO.setHfqCurrent(latestStock.getHfqClose());
                strategyBO.setTradeDate(DateUtils.formatDate(latestStock.getTradeDate(), DateUtils.DATE_FORMAT));

                if (lowest.add(interval).compareTo(latestStock.getHfqClose()) == 1) {
                    // 进入最低价 + 5%的区域
                    strategyBO.setLow(lowest);
                    strategyBO.setHigh(lowest.add(interval));


                    strategyBO.setDate(stockDao.countStockDataByCode(stockBase.getStockCode(), null));
                    Map<String, BigDecimal> lowPrice = Maps.newHashMap();
                    lowPrice.put("lowPrice", latestStock.getHfqClose());
                    strategyBO.setLowPriceDate(stockDao.countStockDataByCode(stockBase.getStockCode(), lowPrice));
                    Map<String, BigDecimal> lowVolume = Maps.newHashMap();
                    lowVolume.put("lowVolume", latestStock.getTradeCount());
                    strategyBO.setLowVolumeDate(stockDao.countStockDataByCode(stockBase.getStockCode(), lowVolume));

                    // 判断成交量，如果成交量小于5%，或者大于40%的时间，重点标注
                    if (strategyBO.getDate() * 0.05 > strategyBO.getLowVolumeDate() || strategyBO.getDate() * 0.4 < strategyBO.getLowVolumeDate()) {
                        strategyBO.setBuy(true);
                    } else {
                        strategyBO.setBuy(false);
                    }

                    strategyBOList.add(strategyBO);
                } else {
                    // 进入最低价 + 5% ~ 最低价 + 15%的区域
                    strategyBO.setLow(lowest.add(interval));
                    strategyBO.setHigh(interval.multiply(BigDecimal.valueOf(4)).add(lowest));
                    strategyBO.setBuyObserve(true);
                    strategyBOList.add(strategyBO);
                }
            } else if (highest.subtract(interval.multiply(BigDecimal.valueOf(4))).compareTo(latestStock.getHfqClose()) == -1) {

                StockStrategyBO strategyBO = new StockStrategyBO();
                strategyBO.setStockCode(stockBase.getStockCode());
                strategyBO.setStockName(stockBase.getStockName());
                strategyBO.setCurrent(latestStock.getClose());
                strategyBO.setHfqCurrent(latestStock.getHfqClose());
                strategyBO.setTradeDate(DateUtils.formatDate(latestStock.getTradeDate(), DateUtils.DATE_FORMAT));

                if (highest.subtract(interval).compareTo(latestStock.getHfqClose()) == -1) {
                    // 进入最高价 - 5%的区域
                    strategyBO.setHigh(highest);
                    strategyBO.setLow(highest.subtract(interval));
                    strategyBO.setSell(true);

                    strategyBO.setDate(stockDao.countStockDataByCode(stockBase.getStockCode(), null));
                    strategyBO.setHighPriceDate(stockDao.countStockDataByCode(stockBase.getStockCode(), (Map<String, BigDecimal>) Maps.newHashMap().put("highPrice", latestStock.getHfqClose())));
                    strategyBO.setHighVolumeDate(stockDao.countStockDataByCode(stockBase.getStockCode(), (Map<String, BigDecimal>) Maps.newHashMap().put("highVolume", latestStock.getTradeCount())));

                    strategyBOList.add(strategyBO);
                } else {
                    // 进入最高价 - 15% ~ 最高价 - 5%的区域
                    strategyBO.setHigh(highest.subtract(interval));
                    strategyBO.setLow(highest.subtract(interval.multiply(BigDecimal.valueOf(4))));
                    strategyBO.setSellObserve(true);
                    strategyBOList.add(strategyBO);
                }
            }
        }

        // 进入低档和高档区间后，发邮件提升
        if (strategyBOList.size() > 0) {
            String title = "股票买卖提示邮件";
            StringBuilder body = new StringBuilder();
            for (StockStrategyBO bo : strategyBOList) {

                if (bo.isBuy()) {
                    body.append("股票: <font color='red'><b>").append(bo.getStockName()).append("[").append(bo.getStockCode()).append("]</b></font>, ");
                    body.append("已进入低估区，截至到: ").append(bo.getTradeDate()).append(", ");
                    body.append("现价为: ").append(bo.getCurrent()).append(", 后复权价格为: ").append(bo.getHfqCurrent());
                    body.append("。低估区域: ").append(bo.getLow()).append(" ~ ").append(bo.getHigh()).append("。");
                    body.append("交易天数共计: ").append(bo.getDate()).append("天，");
                    body.append("比当前价格低的天数: ").append(bo.getLowPriceDate()).append("天，");
                    body.append("比当前交易量低的天数: ").append(bo.getLowVolumeDate()).append("天，");
                    body.append("需要密切关注。<br/><br/>");

                } else if (bo.isBuyObserve()) {
                    body.append("股票: ").append(bo.getStockName()).append("[").append(bo.getStockCode()).append("], ");
                    body.append("已进入低档区，现价为: ").append(bo.getCurrent()).append(", 后复权价格为: ").append(bo.getHfqCurrent());
                    body.append("。低档区域: ").append(bo.getLow()).append(" ~ ").append(bo.getHigh()).append("。<br/><br/>");
                } else if (bo.isSell()) {
                    body.append("股票: <font color='blue'><b>").append(bo.getStockName()).append("[").append(bo.getStockCode()).append("]</b></font>, ");
                    body.append("已进入高估区，截至到: ").append(bo.getTradeDate()).append(", ");
                    body.append("现价为: ").append(bo.getCurrent()).append(", 后复权价格为: ").append(bo.getHfqCurrent());
                    body.append("。高估区域: ").append(bo.getLow()).append(" ~ ").append(bo.getHigh()).append("。");
                    body.append("交易天数共计: ").append(bo.getDate()).append("天，");
                    body.append("比当前价格高的天数: ").append(bo.getHighPriceDate()).append("天，");
                    body.append("比当前交易量高的天数: ").append(bo.getHighVolumeDate()).append("天，");
                    body.append("需要密切关注。<br/><br/>");
                } else if (bo.isSellObserve()) {
                    body.append("股票: ").append(bo.getStockName()).append("[").append(bo.getStockCode()).append("], ");
                    body.append("已进入高档区，现价为: ").append(bo.getCurrent()).append(", 后复权价格为: ").append(bo.getHfqCurrent());
                    body.append("。高档区域: ").append(bo.getLow()).append(" ~ ").append(bo.getHigh()).append("。<br/><br/>");
                }
            }

            try {
                MailUtils.sendMail(title, body.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        log.info("买卖区判断定时任务结束.");
    }

    // 可转债定时任务
    public void convertibleBond() {
        /*
        可转债计算公式
        1. 转股价值
            转股价值 = 100 / 转股价 x 正股现价
        2. 溢价率
            溢价率 =（转债现价 - 转股价值）/ 转股价值
        3. 标准券折算率
            标准券折算率=中登公布的标准券折算率/转债现价
        4. 回售触发价
            回售触发价：募集说明书中约定的连续N日低于转股价的X%可提前回售。
            回售触发价=当期转股价*X%
        5. 税前及税后收益率计算
            税前及税后收益率计算：使用XIRR函数。
         */
    }
}
