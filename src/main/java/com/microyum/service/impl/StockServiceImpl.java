package com.microyum.service.impl;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.DateUtils;
import com.microyum.dao.jdbc.MyStockJdbcDao;
import com.microyum.dao.jpa.MyStockBaseDao;
import com.microyum.dao.jpa.MyStockDailyStrategyDao;
import com.microyum.dao.jpa.MyStockDataDao;
import com.microyum.dto.CalculateStockTransactionCostDto;
import com.microyum.dto.StockBaseDto;
import com.microyum.dto.StockBaseListDto;
import com.microyum.model.stock.MyStockBase;
import com.microyum.model.stock.MyStockDailyStrategy;
import com.microyum.model.stock.MyStockData;
import com.microyum.service.RepairService;
import com.microyum.service.StockService;
import com.microyum.strategy.StockStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StockServiceImpl implements StockService {

    @Autowired
    private MyStockJdbcDao stockJdbcDao;
    @Autowired
    private MyStockBaseDao stockBaseDao;
    @Autowired
    private MyStockDataDao stockDataDao;
    @Autowired
    private StockStrategy stockStrategy;
    @Autowired
    private MyStockDailyStrategyDao dailyStrategyDao;
    @Autowired
    private RepairService repairService;

    @Value("${python.script.repair.stock.hfqdata}")
    private String repairStockScript;

    @Override
    public BaseResponseDTO referStockList(int pageNo, int pageSize, String stock) {

        List<StockBaseListDto> listStock = stockJdbcDao.referStockList(pageNo, pageSize, stock);

        BaseResponseDTO responseDTO = new BaseResponseDTO(HttpStatus.OK_LAYUI, listStock);
        responseDTO.setCount(stockJdbcDao.countAllStockBase(stock));

        return responseDTO;
    }

    @Override
    public BaseResponseDTO referStockDetail(String area, String stockCode, String startDate, String endDate) {

        List<MyStockData> stockDaoList = stockJdbcDao.referStockData(area, stockCode, startDate, endDate);
        return new BaseResponseDTO(HttpStatus.OK, stockDaoList);
    }

    @Override
    public BaseResponseDTO referStockTradeDayDetail(String area, String stockCode) {

        return new BaseResponseDTO(HttpStatus.OK, stockJdbcDao.referLatestStockData(area, stockCode));
    }

    @Override
    public BaseResponseDTO calculateStockTransactionCost(CalculateStockTransactionCostDto dto) {

        BigDecimal buyTotal = dto.getStockPrice().multiply(dto.getStockCount());
        BigDecimal sellTotal = dto.getStockObjectPrice().multiply(dto.getStockCount());

        DecimalFormat format = new DecimalFormat("0.00");

        // ----------------------------------------------------------------
        // 印花税(卖方单边征收，收取成交金额的0.1%)
        // ----------------------------------------------------------------
        BigDecimal stampDuty = sellTotal.multiply(BigDecimal.valueOf(0.001));
        dto.setStampDuty(format.format(stampDuty));

        // ----------------------------------------------------------------
        // 佣金(最少10元，买入卖出都会收取)
        // ----------------------------------------------------------------
        BigDecimal buyCommission = buyTotal.multiply(dto.getCommissionRate()).multiply(BigDecimal.valueOf(0.01));
        if (buyCommission.compareTo(BigDecimal.valueOf(5L)) == -1) {
            buyCommission = BigDecimal.valueOf(5L);
        }

        BigDecimal sellCommission = sellTotal.multiply(dto.getCommissionRate()).multiply(BigDecimal.valueOf(0.01));
        if (sellCommission.compareTo(BigDecimal.valueOf(5L)) == -1) {
            sellCommission = BigDecimal.valueOf(5L);
        }

        BigDecimal commission = buyCommission.add(sellCommission);
        dto.setCommission(format.format(commission));

        // ----------------------------------------------------------------
        // 监管费(成交金额的0.002%，双向收取)
        // ----------------------------------------------------------------
        BigDecimal supervisionFee = buyTotal.multiply(BigDecimal.valueOf(0.00002)).add(sellTotal.multiply(BigDecimal.valueOf(0.00002)));
        dto.setSupervisionFee(format.format(supervisionFee));

        // ----------------------------------------------------------------
        // 过户费(成交金额的0.002%)
        // ----------------------------------------------------------------
        BigDecimal transferFee = sellTotal.multiply(BigDecimal.valueOf(0.00002));
        dto.setTransferFee(format.format(transferFee));

        // ----------------------------------------------------------------
        // 经手费
        // ----------------------------------------------------------------
        BigDecimal exchangeFee = BigDecimal.ZERO;
        if (StringUtils.equalsAny(dto.getTradingObject(), "1", "3")) {
            exchangeFee = buyTotal.multiply(BigDecimal.valueOf(0.000087));
        } else {
            exchangeFee = buyTotal.multiply(BigDecimal.valueOf(0.000001));
        }
        dto.setExchangeFee(format.format(exchangeFee));

        BigDecimal totalTax = stampDuty.add(commission).add(supervisionFee).add(transferFee).add(exchangeFee);
        // 税费合计
        dto.setTotalTax(format.format(totalTax));

        // 占额交易(%)
        dto.setShareTrading(format.format(totalTax.multiply(BigDecimal.valueOf(100)).divide(buyTotal, RoundingMode.UP)));

        // 盈利
        dto.setProfit(format.format(sellTotal.subtract(buyTotal).subtract(totalTax)));

        return new BaseResponseDTO(HttpStatus.OK, dto);
    }

    public void repairStockData(String area, String stockCode) {

        log.info("开始补齐股票数据...");
        Process proc;
        try {
            if (StringUtils.isNotBlank(stockCode)) {
                proc = Runtime.getRuntime().exec(repairStockScript + " " + area + " " + stockCode);
            } else {
                proc = Runtime.getRuntime().exec(repairStockScript);
            }

            if (proc.waitFor() == 0) {
                log.info("补齐股票数据结束.");
                return;
            }

        } catch (Exception e) {
            log.error("补齐股票数据失败, ", e);
            return;
        }
    }

    @Override
    public BaseResponseDTO referStockBase(Long id) {

        return new BaseResponseDTO(HttpStatus.OK, stockBaseDao.findById(id));
    }

    @Override
    public BaseResponseDTO saveStockBase(StockBaseDto stockBase) {

        if (StringUtils.isBlank(stockBase.getStockCode())) {
            return new BaseResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        MyStockBase entity = new MyStockBase();
        BeanUtils.copyProperties(stockBase, entity);

        // 设置明细数据开始日期
        // 周六、周日的场合，顺延到周一
        // 周一到周五，判断如果是9:30之前，则设定为当日，否则就设置为次日
        Calendar calendar = Calendar.getInstance();
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayWeek == 1) {
            entity.setDetailDate(DateUtils.addDays(new Date(), 1));
        } else if (dayWeek == 7) {
            entity.setDetailDate(DateUtils.addDays(new Date(), 2));
        } else {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // 周五开盘后，设置为下周一
            if ((dayWeek == 6) && ((hour > 9) || (hour == 9) && minute >= 30)) {
                entity.setDetailDate(DateUtils.addDays(new Date(), 3));
            } else if ((hour < 9) || (hour == 9 && minute < 30)) {
                entity.setDetailDate(new Date());
            } else {
                entity.setDetailDate(DateUtils.addDays(new Date(), 1));
            }
        }

        entity.setListingDate(DateUtils.parseDate(stockBase.getListingDate(), DateUtils.DATE_FORMAT));
        entity.setCirculationCapital(Double.valueOf(stockBase.getCirculationCapital()));
        entity.setTotalCapital(Double.valueOf(stockBase.getTotalCapital()));
        entity.setObserve(Byte.valueOf("1"));
        entity.setListSort(stockBase.getStockCode());


        // 調用脚本補全數據
        this.repairStockData(entity.getArea(), entity.getStockCode());

        // 獲取補全的數據的開始日期，更新到MyStockBase表
        Date minTradeDate = stockDataDao.findMinTradeDateByStock(entity.getArea(), entity.getStockCode());
        if (minTradeDate != null) {
            entity.setDailyDate(minTradeDate);
        }

        stockBaseDao.save(entity);

        // 补齐策略数据
        repairService.repairStrategyData(entity.getArea(), entity.getStockCode());

        return new BaseResponseDTO(HttpStatus.OK);
    }

    @Override
    public BaseResponseDTO updateStockBase(MyStockBase stockBase) {

        stockBase.setLastUpdateTime(new Date());
        stockBaseDao.save(stockBase);
        return new BaseResponseDTO(HttpStatus.OK);
    }

    @Override
    public BaseResponseDTO deleteStockBase(Long id) {

        stockBaseDao.deleteById(id);
        return new BaseResponseDTO(HttpStatus.OK);
    }

    @Override
    public BaseResponseDTO checkStockExist(String area, String code) {

        MyStockBase stockBase = stockBaseDao.findByAreaAndStockCode(area, code);
        return new BaseResponseDTO(HttpStatus.OK, stockBase != null ? true : false);
    }

    @Override
    public BaseResponseDTO referEntityList(Integer type) {

        List<Map<String, String>> result = stockJdbcDao.referEntityList(type);
        return new BaseResponseDTO(HttpStatus.OK, result);
    }

    @Override
    public BaseResponseDTO makeupStrategy(String date) {

        Date calDate = DateUtils.parseDate(date, DateUtils.DATE_FORMAT_COMP);

        while (!DateUtils.isSameDay(calDate, new Date())) {

            log.info(DateUtils.formatDate(calDate, DateUtils.DATE_FORMAT) + " start ...");

            if (!stockStrategy.isTradingDay(calDate)) {
                calDate = DateUtils.addDays(calDate, 1);
                continue;
            }

            List<Map<String, String>> makeupDatas = stockJdbcDao.referMakeupStrategyDate(calDate);
            for (Map<String, String> makeupData : makeupDatas) {

                // 已经有策略的数据不做补齐
                if (StringUtils.isNotBlank(makeupData.get("strategy"))) {
                    continue;
                }

                MyStockDailyStrategy dailyStrategy = stockStrategy.calcStockValueRangeByDate(makeupData.get("area"), makeupData.get("stockCode"), calDate);
                if (dailyStrategy != null) {
                    dailyStrategyDao.save(dailyStrategy);
                }
            }
            log.info(DateUtils.formatDate(calDate, DateUtils.DATE_FORMAT) + " end .");

            calDate = DateUtils.addDays(calDate, 1);
        }

        return new BaseResponseDTO(HttpStatus.OK);
    }

    @Override
    public BaseResponseDTO removeRepeatStrategyData() {

        List<MyStockBase> stockBases = stockBaseDao.findAll();

        for (MyStockBase stockBase : stockBases) {

            List<MyStockDailyStrategy> strategyList = dailyStrategyDao.findByStock(stockBase.getArea(), stockBase.getStockCode());

            for (MyStockDailyStrategy strategy : strategyList) {

                List<MyStockDailyStrategy> strategys = dailyStrategyDao.findByStockAndTradeDate(strategy.getArea(), strategy.getStockCode(), DateUtils.formatDate(strategy.getTradeDate(), DateUtils.DATE_FORMAT));
                if (strategys.size() > 1) {
                    dailyStrategyDao.deleteById(strategy.getId());
                }
            }
        }

        return new BaseResponseDTO(HttpStatus.OK);
    }
}
