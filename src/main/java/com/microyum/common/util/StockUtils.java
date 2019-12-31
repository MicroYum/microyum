package com.microyum.common.util;

import com.google.common.collect.Maps;
import com.microyum.bo.StockStrategyBO;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StockUtils {

    /**
     * 解析新浪股票数据
     *
     * @param stockCode
     * @param strStock
     * @return
     */
    public static Map<String, String> parseSinaStock(String stockCode, String strStock) {

        Map<String, String> result = Maps.newHashMap();
        String content = "";
        Pattern p = Pattern.compile("\"(.*?)\"");
        Matcher m = p.matcher(strStock);
        while (m.find()) {
            content = m.group().replace("\"", "");
        }

        String[] stock = content.split(",");

        result.put("stockCode", stockCode);
        result.put("tradeDate", stock[30]);
        result.put("tradeTime", stock[31]);
        result.put("open", stock[1]);       // 今日开盘价
        result.put("close", stock[3]);      // 最近成交价
        result.put("high", stock[4]);       // 最高成交价
        result.put("low", stock[5]);        // 最低成交价


        BigDecimal chg = new BigDecimal(stock[3]).subtract(new BigDecimal(stock[2]));

        // 涨跌额: 最近成交价 - 昨日收盘价
        result.put("chg", String.valueOf(chg));
        // 涨跌幅(%): (最近成交价 - 昨日收盘价) / 昨日收盘价 * 100
        result.put("percent", String.valueOf(chg.divide(new BigDecimal(stock[2]), 4).multiply(BigDecimal.valueOf(100))));

        result.put("tradeCount", stock[8]);
        result.put("tradeAmount", stock[9]);

        return result;
    }

    /**
     * 高估买入
     *
     * @param strategyBO
     * @return 邮件消息
     */
    public static String overrateBuy(StockStrategyBO strategyBO) {

        return null;
    }

    /**
     * 高估观察
     *
     * @param strategyBO
     * @return 邮件消息
     */
    public static String overrateObserve(StockStrategyBO strategyBO) {
        return null;
    }

    /**
     * 低估买入
     *
     * @param strategyBO
     * @return 邮件消息
     */
    public static String underrateBuy(StockStrategyBO strategyBO) {
        return null;
    }

    /**
     * 低估观察
     *
     * @param strategyBO
     * @return 邮件消息
     */
    public static String underrateObserve(StockStrategyBO strategyBO) {
        return null;
    }
}
