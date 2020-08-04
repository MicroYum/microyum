package com.microyum.schedule;

import com.google.common.collect.Lists;
import com.microyum.common.Constants;
import com.microyum.common.util.DateUtils;
import com.microyum.common.util.StockUtils;
import com.microyum.dao.jdbc.MyBondJdbcDao;
import com.microyum.dao.jpa.MyBondDataDao;
import com.microyum.model.bond.MyBondBase;
import com.microyum.model.bond.MyBondData;
import com.microyum.strategy.StockStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 获取债券实时数据定时任务
 */
@Component
@Slf4j
public class ReferBondDataSchedule {

    @Autowired
    private MyBondJdbcDao bondJdbcDao;

    @Autowired
    private MyBondDataDao bondDataDao;

    @Autowired
    private StockStrategy stockStrategy;

    @Scheduled(cron = "30 0/1 * * * ? ")
    public synchronized void getRealtimeBondBySina() {

        // 定时任务运行时间每周一到周五，9:30 ~ 11:30, 13:00 ~ 15:00
        if (!stockStrategy.isTradingDay()) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
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

        log.info("获取债券数据定时任务开始...");

        List<MyBondBase> bondBaseList = bondJdbcDao.getObservedList();

        List<String> urls = Lists.newArrayList();
        List<String> bonds = Lists.newArrayList();
        for (MyBondBase bondBase : bondBaseList) {
            bonds.add(bondBase.getArea() + bondBase.getBondCode());

            if (bonds.size() == 300) {
                urls.add(Constants.STOCK_SINA_URL + String.join(",", bonds));
                bonds = Lists.newArrayList();
            }
        }

        if (bonds.size() != 0) {
            urls.add(Constants.STOCK_SINA_URL + String.join(",", bonds));
        }

        for (String url : urls) {
            updateBondData(url);
        }

        log.info("获取债券数据定时任务结束.");
    }

    @Async
    public void updateBondData(String requestUrl) {

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

                    String bondCode = line.substring(11, 19);

                    // 解析响应内容
                    Map<String, String> mapStack = StockUtils.parseSinaStock(bondCode.substring(2), line);

                    // 交易量为0，说明可能今天停市，或者股票停牌
                    if (Integer.parseInt(mapStack.get("tradeCount").trim()) == 0) {
                        continue;
                    }

                    MyBondData bondData = this.tidyBondData(mapStack);
                    bondData.setArea(bondCode.substring(0, 2));

                    // insert / update
                    MyBondData bond = bondDataDao.findByAreaAndBondCodeAndTradeDate(bondData.getArea(), bondData.getBondCode(), bondData.getTradeDate());
                    if (bond != null) {
                        bondData.setId(bond.getId());
                    }

                    bondDataDao.save(bondData);
                }
            }
        } catch (Exception e) {
            log.error("请求/解析新浪接口错误", e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("释放新浪接口连接错误", e);
            }
        }
    }

    private MyBondData tidyBondData(Map<String, String> mapStack) {

        MyBondData bondData = new MyBondData();
        bondData.setBondCode(mapStack.get("stockCode"));
        bondData.setTradeDate(DateUtils.parseDate(mapStack.get("tradeDate"), DateUtils.DATE_FORMAT));
        bondData.setOpen(new BigDecimal(mapStack.get("open")));
        bondData.setClose(new BigDecimal(mapStack.get("close")));
        bondData.setHigh(new BigDecimal(mapStack.get("high")));
        bondData.setLow(new BigDecimal(mapStack.get("low")));
        bondData.setPercent(new BigDecimal(mapStack.get("percent")));
        bondData.setChg(new BigDecimal(mapStack.get("chg")));
        bondData.setVolume(new BigDecimal(mapStack.get("tradeCount")));

        return bondData;
    }
}
