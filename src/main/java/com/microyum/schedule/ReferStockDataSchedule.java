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
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${python.script.repair.latest.hfqdata}")
    private String repairLatestScript;

    @Scheduled(cron = "0 0/1 * * * ? ")
    public synchronized void getRealtimeStockBySina() {

        // 定时任务运行时间每周一到周五，9:30 ~ 11:30, 13:00 ~ 15:00
        Calendar calendar = Calendar.getInstance();
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayWeek == 1 || dayWeek == 7) {
            log.info("周六、周日不获取数据.");
            return;
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (!((hour >= 9 && hour <= 11) || (hour >= 13 && hour <= 15))) {
            log.info("现在是：" + hour + "点，股市还未开盘");
            return;
        }

        int minute = calendar.get(Calendar.MINUTE);
        if ((hour == 9 && minute < 30) || (hour == 11 && minute > 30) || (hour == 13 && minute < 1) || (hour == 15 && minute > 1)) {
            log.info("现在是：" + hour + "点" + minute + "分，股市还未开盘");
            return;
        }

        log.info("获取股票数据定时任务开始...");

        CloseableHttpClient httpclient = HttpClients.createDefault();
        List<MyStockBase> stockBaseList = stockJdbcDao.getObservedList();

        List<String> stocks = Lists.newArrayList();
        for (MyStockBase stockBase : stockBaseList) {
            stocks.add(stockBase.getArea() + stockBase.getStockCode());
        }
        String requestUrl = Constants.STOCK_SINA_URL + String.join(",", stocks);

        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(requestUrl);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);

                int index = 0;
                for (String line : result.split("\n")) {
                    // 解析响应内容
                    Map<String, String> mapStack = StockUtils.parseSinaStock(stockBaseList.get(index).getStockCode(), line);
                    index++;

                    // TODO 此处的判断，只是假设，可能会有错误，需要特别注意
                    // 日期不相同的场合，说明可能今天停市，或者股票停牌
                    if (!DateUtils.isSameDay(DateUtils.parseDate(mapStack.get("tradeDate"), DateUtils.DATE_FORMAT), new Date())) {
                        return;
                    }

                    MyStockData stockData = this.tidyStockData(mapStack);
                    stockData.setArea(stockBaseList.get(index).getArea());

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

        log.info("获取股票数据定时任务结束.");
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
        Calendar calendar = Calendar.getInstance();
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayWeek == 1 || dayWeek == 7) {
            log.info("周六、周日不补全后复权数据.");
            return;
        }


        log.info("开始补齐当天的后复权数据...");

        try {
            Runtime.getRuntime().exec(repairLatestScript);
        } catch (Exception e) {
            log.error("补齐后复权数据失败, ", e);
            return;
        }

        log.info("补齐当天的后复权数据结束.");
    }
}
