package com.microyum.schedule;

import com.microyum.common.Constants;
import com.microyum.common.util.DateUtils;
import com.microyum.common.util.StockUtils;
import com.microyum.dao.MyStockDao;
import com.microyum.model.MyStockBase;
import com.microyum.model.MyStockData;
import com.microyum.model.MyStockDataDetail;
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
    private MyStockDao stockDao;

    @Value("${python.script.repair.latest.hfqdata}")
    private String repairLatestScript;

    @Scheduled(cron = "0 0/1 * * * ? ")
    public synchronized void getRealtimeStockBySina() {

        log.info("获取股票数据定时任务开始...");

        // 定时任务运行时间每周一到周五，9:30 ~ 11:30, 13:00 ~ 15:00
        Calendar calendar = Calendar.getInstance();
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayWeek == 1 || dayWeek == 7) {
            log.info("周六、周日不获取数据.");
            return;
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (!((hour >= 9 && hour <= 11) || (hour >= 13 && hour <= 15))) {
            return;
        }

        int minute = calendar.get(Calendar.MINUTE);
        if ((hour == 9 && minute < 30) || (hour == 11 && minute > 30) || (hour == 15 && minute > 1)) {
            return;
        }

        CloseableHttpClient httpclient = HttpClients.createDefault();

        List<MyStockBase> stockBaseList = stockDao.getObservedList();

        try {
            for (MyStockBase stockBase : stockBaseList) {

                String requestUrl = Constants.STOCK_SINA_URL + stockBase.getArea() + stockBase.getStockCode();
                // 创建httpget.
                HttpGet httpget = new HttpGet(requestUrl);
                // 执行get请求.
                CloseableHttpResponse response = httpclient.execute(httpget);
                try {
                    // 获取响应实体
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {
                        // 解析响应内容
                        Map<String, String> mapStack = StockUtils.parseSinaStock(stockBase.getStockCode(), EntityUtils.toString(entity));

                        // TODO 此处的判断，只是假设，可能会有错误，需要特别注意
                        // 日期不相同的场合，说明可能今天停市，或者股票停牌
                        if (!DateUtils.isSameDay(DateUtils.parseDate(mapStack.get("tradeDate"), DateUtils.DATE_FORMAT), new Date())) {
                            return;
                        }

                        MyStockData stockData = this.tidyStockData(mapStack);
                        MyStockDataDetail stockDataDetail = this.tidyStockDataDetail(mapStack);

                        stockDao.saveStockDataDetail(stockDataDetail);
                        MyStockData stock = stockDao.selectTradeDateStock(stockData.getSymbol(), stockData.getTradeDate());
                        if (stock == null) {
                            // insert
                            stockDao.saveStockData(stockData);
                        } else {
                            // update
                            stockDao.updateStockData(stockData);
                        }
                    }
                } finally {
                    response.close();
                }
            }
        } catch (Exception e) {
            log.error("请求/解析新浪股票接口错误", e);
            e.printStackTrace();
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
        stockData.setSymbol(mapStack.get("symbol"));
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

    private MyStockDataDetail tidyStockDataDetail(Map<String, String> mapStack) {

        MyStockDataDetail stockDataDetail = new MyStockDataDetail();
        stockDataDetail.setSymbol(mapStack.get("symbol"));
        stockDataDetail.setTradeDatetime(DateUtils.parseDate(mapStack.get("tradeDate") + " " + mapStack.get("tradeTime"), DateUtils.DATE_TIME_FORMAT));
        stockDataDetail.setCurrent(new BigDecimal(mapStack.get("close")));
        stockDataDetail.setTradeCount(new BigDecimal(mapStack.get("tradeCount")));
        stockDataDetail.setTradeAmount(new BigDecimal(mapStack.get("tradeAmount")));

        return stockDataDetail;
    }

    /**
     * 补齐后复权数据
     */
    @Scheduled(cron = "0 15 15 * * ? ")
    public void repairHfqData() {

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
