package com.microyum.schedule;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.microyum.bo.BuyingStockBO;
import com.microyum.common.Constants;
import com.microyum.common.enums.StockStrategyEnum;
import com.microyum.common.util.DateUtils;
import com.microyum.common.util.MailUtils;
import com.microyum.dao.jdbc.MyFinanceJdbcDao;
import com.microyum.dao.jdbc.MyMailJdbcDao;
import com.microyum.dao.jdbc.MyStockJdbcDao;
import com.microyum.dao.jpa.MyDayOffDao;
import com.microyum.dao.jpa.MyStockDailyStrategyDao;
import com.microyum.dao.jpa.MyUserDao;
import com.microyum.dto.AssetAllocationDto;
import com.microyum.model.common.MyMailTemplate;
import com.microyum.model.stock.MyStockBase;
import com.microyum.model.stock.MyStockDailyStrategy;
import com.microyum.model.common.MyUser;
import com.microyum.strategy.StockStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 投资策略定时任务
 */
@Component
@Slf4j
public class InvestmentStrategySchedule {

    @Autowired
    private MyStockJdbcDao stockJdbcDao;
    @Autowired
    private MyStockDailyStrategyDao dailyStrategyDao;
    @Autowired
    private MyFinanceJdbcDao myFinanceDao;
    @Autowired
    private StockStrategy stockStrategy;
    @Autowired
    private MyUserDao userDao;
    @Autowired
    private MyMailJdbcDao mailDao;

    /**
     * 交易日15:30开始，计算所有股票的价值区间，保存到MyStockDailyStrategy表
     */
    @Scheduled(cron = "0 30 15 * * ? ")
    public void calcValueRange() {

        // 定时任务运行时间每周一到周五
        if (!stockStrategy.isTradingDay()) {
            return;
        }

        // 获取所有股票的列表
        List<MyStockBase> listStock = stockJdbcDao.getObservedListNotIndex();

        for (MyStockBase stockBase : listStock) {

            try {
                MyStockDailyStrategy dailyStrategy = stockStrategy.calcStockValueRange(stockBase);
                if (dailyStrategy != null) {
                    dailyStrategyDao.save(dailyStrategy);
                }
            } catch (Exception e) {
                log.error("计算价值区间出错", stockBase.getStockCode());
            }
        }
    }

    /**
     * 将推荐买入的股票，邮件发送给管理员
     */
    @Scheduled(cron = "0 45 15 * * ? ")
    public void mailBuyingStock() {

        // 定时任务运行时间每周一到周五
        if (!stockStrategy.isTradingDay()) {
            return;
        }

        MyMailTemplate mailBuyingTemplate = mailDao.findTemplateByName(Constants.MAIL_NAME_BUYING_STOCK);
        MyMailTemplate mailBuyingTableTemplate = mailDao.findTemplateByName(Constants.MAIL_NAME_BUYING_STOCK_TABLE);

        List<BuyingStockBO> stockBOList = stockJdbcDao.referBuyingStock();
        StringBuilder builder = new StringBuilder();
        for (BuyingStockBO stockBO : stockBOList) {
            String mailTable = mailBuyingTableTemplate.getMailBody();
            mailTable = mailTable.replace(":stock_code", stockBO.getStockCode());
            mailTable = mailTable.replace(":stock_name", stockBO.getStockName());
            mailTable = mailTable.replace(":latest_price", String.valueOf(stockBO.getLatestPrice()));
            mailTable = mailTable.replace(":latest_hfq_price", String.valueOf(stockBO.getLatestHfqPrice()));
            mailTable = mailTable.replace(":buying_min", String.valueOf(stockBO.getBuyingMin()));
            mailTable = mailTable.replace(":buying_max", String.valueOf(stockBO.getBuyingMax()));
            mailTable = mailTable.replace(":under_min", String.valueOf(stockBO.getUnderMin()));
            mailTable = mailTable.replace(":under_max", String.valueOf(stockBO.getUnderMax()));
            mailTable = mailTable.replace(":trade_count", String.valueOf(stockBO.getTradeCount()));
            mailTable = mailTable.replace(":price_rate", String.valueOf(stockBO.getPriceRate()));
            mailTable = mailTable.replace(":volume_rate", String.valueOf(stockBO.getVolumeRate()));
            builder.append(mailTable);
        }

        String mailBody = mailBuyingTemplate.getMailBody();
        mailBody = mailBody.replace(":table_content", builder.toString());

        // 发送邮件给管理员
        MyUser user = userDao.findByUId(1L);
        try {
            MailUtils.sendMail("买入股票提示邮件", user.getEmail(), user.getNickName(), mailBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 17 * * ? ")
    public void personalValueInterval() {

        // 检索Trader Account中已配置的账号
        List<AssetAllocationDto> accountDtos = myFinanceDao.assetAllocationAll();

        Map<Long, List<Map<String, Object>>> mapAccount = Maps.newHashMap();
        for (AssetAllocationDto dto : accountDtos) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("nickName", dto.getNickName());
            map.put("trader", dto.getTrader());
            map.put("account", dto.getAccount());
            map.put("area", dto.getArea());
            map.put("stockCode", dto.getStockCode());
            map.put("stockName", dto.getAssetName());

            if (mapAccount.containsKey(dto.getUid())) {
                mapAccount.get(dto.getUid()).add(map);
            } else {
                List<Map<String, Object>> list = Lists.newArrayList();
                list.add(map);
                mapAccount.put(dto.getUid(), list);
            }
        }

        String title = "股票买卖提示邮件";

        for (Long uid : mapAccount.keySet()) {
            MyUser user = userDao.findByUId(uid);

            // 0表示不发送邮件
            if (user.getNotification().intValue() == 0) {
                continue;
            }

            StringBuilder body = new StringBuilder();
            body.append(" Dear, ").append(user.getNickName()).append("<br/><br/>");
            Map<String, Boolean> repetition = Maps.newHashMap();

            for (Map<String, Object> item : mapAccount.get(uid)) {

                // 避免同一个人名下，又多个股票账户中出现同一个股票而造成的反复
                if (repetition.containsKey(item.get("stockCode"))) {
                    continue;
                } else {
                    repetition.put(String.valueOf(item.get("stockCode")), true);
                }

                // 2、匹配MyStockDailyStrategy表，如果条件满足则列入List
                MyStockDailyStrategy strategy = dailyStrategyDao.findByStockAndTradeDate(String.valueOf(item.get("area")), String.valueOf(item.get("stockCode")), DateUtils.getCurrentDate()).get(0);

                if (strategy.getStrategy().intValue() == StockStrategyEnum.STRATEGY_BUYING.getCode()) {
                    body.append("股票: <font color='red'><b>").append(item.get("stockName")).append("[").append(strategy.getStockCode()).append("]</b></font>已进入买入区, ");
                    body.append("交易日: ").append(DateUtils.formatDate(strategy.getTradeDate(), DateUtils.DATE_FORMAT)).append(", ");
                    body.append("现价为: ").append(strategy.getLatestPrice()).append(", 后复权价格为: ").append(strategy.getLatestHfqPrice());
                    body.append("。买入区域(后复权): ").append(strategy.getBuyingMin()).append(" ~ ").append(strategy.getBuyingMax()).append("，建议买入<br/><br/>");
                } else if (strategy.getStrategy().intValue() == StockStrategyEnum.STRATEGY_UNDER_VALUE.getCode()) {
                    body.append("股票: ").append(item.get("stockName")).append("[").append(strategy.getStockCode()).append("]已进入低估区, ");
                    body.append("交易日: ").append(DateUtils.formatDate(strategy.getTradeDate(), DateUtils.DATE_FORMAT)).append(", ");
                    body.append("现价为: ").append(strategy.getLatestPrice()).append(", 后复权价格为: ").append(strategy.getLatestHfqPrice());
                    body.append("。低估区域(后复权): ").append(strategy.getUnderMin()).append(" ~ ").append(strategy.getUnderMax()).append("。<br/><br/>");
                } else if (strategy.getStrategy().intValue() == StockStrategyEnum.STRATEGY_OVER_VALUE.getCode()) {
                    body.append("股票: ").append(item.get("stockName")).append("[").append(strategy.getStockCode()).append("]已进入高估区, ");
                    body.append("交易日: ").append(DateUtils.formatDate(strategy.getTradeDate(), DateUtils.DATE_FORMAT)).append(", ");
                    body.append("现价为: ").append(strategy.getLatestPrice()).append(", 后复权价格为: ").append(strategy.getLatestHfqPrice());
                    body.append("。高估区域(后复权): ").append(strategy.getOverMin()).append(" ~ ").append(strategy.getOverMax()).append("。<br/><br/>");
                } else if (strategy.getStrategy().intValue() == StockStrategyEnum.STRATEGY_SELLING.getCode()) {
                    body.append("股票: <font color='blue'><b>").append(item.get("stockName")).append("[").append(strategy.getStockCode()).append("]</b></font>已进入卖出区, ");
                    body.append("交易日: ").append(DateUtils.formatDate(strategy.getTradeDate(), DateUtils.DATE_FORMAT)).append(", ");
                    body.append("现价为: ").append(strategy.getLatestPrice()).append(", 后复权价格为: ").append(strategy.getLatestHfqPrice());
                    body.append("。卖出区域(后复权): ").append(strategy.getSellingMin()).append(" ~ ").append(strategy.getSellingMax()).append("，请密切关注<br/><br/>");
                }
            }

            // 3、将List中的标的信息，邮件发送给user表中的email账号
            try {
                MailUtils.sendMail(title, user.getEmail(), user.getNickName(), body.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
