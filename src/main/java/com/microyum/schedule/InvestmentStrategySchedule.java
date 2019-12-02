package com.microyum.schedule;

import com.microyum.dao.MyStockDao;
import com.microyum.dto.StockLatestDataDTO;
import com.microyum.model.MyStockBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 投资策略定时任务
 */
@Component
@Slf4j
public class InvestmentStrategySchedule {

    @Autowired
    private MyStockDao stockDao;

    // 进入买卖区间，每天9:00执行
    public void valueInterval() {

        // 获取所有股票的列表
        List<MyStockBase> listStock = stockDao.getObservedListNotIndex();

        for (MyStockBase stockBase : listStock) {
            // 获取所有后复权数据的最高点记录、最低点记录
            BigDecimal highest = stockDao.getHighestStock(stockBase.getStockCode());
            BigDecimal lowest = stockDao.getLowestStock(stockBase.getStockCode());


            // 将数据分为四档：低估5%, 低档15%, 中档60%, 高档15%, 高估5%
            BigDecimal interval = highest.subtract(lowest).divide(BigDecimal.valueOf(20));

            StockLatestDataDTO latestStock = stockDao.referLatestStockData(stockBase.getStockCode());

            if (lowest.add(interval.multiply(BigDecimal.valueOf(4))).compareTo(latestStock.getHfqClose()) != -1) {

                if (lowest.add(interval).compareTo(latestStock.getHfqClose()) != -1) {
                    // 进入最低价 + 5%的区域
                } else {
                    // 进入最低价 + 5% ~ 最低价 + 15%的区域
                }
            } else if (highest.subtract(interval.multiply(BigDecimal.valueOf(4))).compareTo(latestStock.getHfqClose()) != -1) {

                if (highest.subtract(interval).compareTo(latestStock.getHfqClose()) != -1) {
                    // 进入最高价 - 5%的区域
                } else {
                    // 进入最高价 - 15% ~ 最高价 - 5%的区域
                }
            }


        }


        // 进入低档和高档区间后，发邮件提升
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
