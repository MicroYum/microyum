package com.microyum.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 投资策略定时任务
 */
@Component
@Slf4j
public class InvestmentStrategySchedule {

    // 进入买卖区间

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
