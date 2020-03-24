package com.microyum.schedule;

import com.google.common.collect.Lists;
import com.microyum.common.Constants;
import com.microyum.common.util.DateUtils;
import com.microyum.common.util.StockUtils;
import com.microyum.dao.jdbc.MyStockJdbcDao;
import com.microyum.dao.jpa.MyStockDataDao;
import com.microyum.dao.jpa.MyStockDataDetailDao;
import com.microyum.model.stock.MyStockBase;
import com.microyum.model.stock.MyStockData;
import com.microyum.model.stock.MyStockDataDetail;
import com.microyum.strategy.StockStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 获取股票实时数据定时任务
 */
@Component
@Slf4j
public class ReferStockDataSchedule {

    @Autowired
    private MyStockJdbcDao stockJdbcDao;
    @Autowired
    private MyStockDataDao stockDataDao;
    @Autowired
    private StockStrategy stockStrategy;

    @Value("${python.script.repair.latest.hfqdata}")
    private String repairLatestScript;

    @Scheduled(cron = "0 0/1 * * * ? ")
    public synchronized void getRealtimeStockBySina() {

        // 定时任务运行时间每周一到周五，9:30 ~ 11:30, 13:00 ~ 15:00
        if (!stockStrategy.isTradingDay()) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (!((hour >= 9 && hour <= 11) || (hour >= 13 && hour <= 15))) {
            log.debug("现在是：" + hour + "点，股市还未开盘");
            return;
        }

        int minute = calendar.get(Calendar.MINUTE);
        if ((hour == 9 && minute < 30) || (hour == 11 && minute > 30) || (hour == 13 && minute < 1) || (hour == 15 && minute > 1)) {
            log.debug("现在是：" + hour + "点" + minute + "分，股市还未开盘");
            return;
        }

        log.info("获取股票数据定时任务开始...");

        List<MyStockBase> stockBaseList = stockJdbcDao.getObservedList();

        List<String> urls = Lists.newArrayList();
        List<String> stocks = Lists.newArrayList();
        for (MyStockBase stockBase : stockBaseList) {
            stocks.add(stockBase.getArea() + stockBase.getStockCode());

            if (stocks.size() == 300) {
                urls.add(Constants.STOCK_SINA_URL + String.join(",", stocks));
                stocks = Lists.newArrayList();
            }
        }

        if (stocks.size() != 0) {
            urls.add(Constants.STOCK_SINA_URL + String.join(",", stocks));
        }

        for (String url : urls) {
            updateNewStockData(url);
        }

        log.info("获取股票数据定时任务结束.");
    }

    @Async
    public void updateNewStockData(String requestUrl) {

        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(requestUrl);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);

                for (String line : result.split("\n")) {

                    String stockCode = line.substring(11, 19);

                    // 解析响应内容
                    Map<String, String> mapStack = StockUtils.parseSinaStock(stockCode.substring(2), line);

                    // 股票停牌
                    if (new BigDecimal(mapStack.get("open")).equals(BigDecimal.ZERO)) {
                        return;
                    }

                    MyStockData stockData = this.tidyStockData(mapStack);
                    stockData.setArea(stockCode.substring(0, 2));

                    // insert / update
                    MyStockData stock = stockJdbcDao.selectTradeDateStock(stockData.getStockCode(), stockData.getArea(), stockData.getTradeDate());
                    if (stock != null) {
                        stockData.setId(stock.getId());
                    }
                    stockDataDao.save(stockData);
                }
            }
        } catch (Exception e) {
            log.error("请求/解析新浪股票接口错误", e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("释放新浪股票接口连接错误", e);
            }
        }
    }

    private MyStockData tidyStockData(Map<String, String> mapStack) {

        MyStockData stockData = new MyStockData();
        stockData.setStockCode(mapStack.get("stockCode"));
        stockData.setTradeDate(DateUtils.parseDate(mapStack.get("tradeDate"), DateUtils.DATE_FORMAT));
        stockData.setOpen(new BigDecimal(mapStack.get("open")));
        stockData.setClose(new BigDecimal(mapStack.get("close")));
        stockData.setHigh(new BigDecimal(mapStack.get("high")));
        stockData.setLow(new BigDecimal(mapStack.get("low")));
        stockData.setPercent(new BigDecimal(mapStack.get("percent")));
        stockData.setChg(new BigDecimal(mapStack.get("chg")));
        stockData.setTradeCount(new BigDecimal(mapStack.get("tradeCount")));
        stockData.setTradeAmount(new BigDecimal(mapStack.get("tradeAmount")));

        return stockData;
    }

    /**
     * 每天收盘10分钟猴开始补齐后复权数据
     */
    @Scheduled(cron = "0 10 15 * * ? ")
    public void repairHfqData() {

        // 定时任务运行时间每周一到周五
        if (!stockStrategy.isTradingDay()) {
            return;
        }

        Process proc;
        try {
            log.info("开始补齐当天的后复权数据...");
            proc = Runtime.getRuntime().exec(repairLatestScript);

            if (proc.waitFor() == 0) {
                log.info("补齐当天的后复权数据结束.");
                return;
            }
        } catch (Exception e) {
            log.error("补齐后复权数据失败, ", e);
            return;
        }
    }
}
