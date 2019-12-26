package com.microyum.service.impl;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.DateUtils;
import com.microyum.dao.jdbc.MyStockJdbcDao;
import com.microyum.dao.jpa.MyStockBaseDao;
import com.microyum.dto.CalculateStockTransactionCostDto;
import com.microyum.dto.StockBaseDto;
import com.microyum.model.stock.MyStockBase;
import com.microyum.model.stock.MyStockData;
import com.microyum.service.StockService;
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

@Service
@Slf4j
public class StockServiceImpl implements StockService {

    @Autowired
    private MyStockJdbcDao stockJdbcDao;
    @Autowired
    private MyStockBaseDao stockBaseDao;

    @Value("${python.script.repair.stock.hfqdata}")
    private String repairStockScript;

    @Override
    public BaseResponseDTO referStockList(int pageNo, int pageSize, String stock) {

        List<MyStockBase> listStock = stockJdbcDao.referStockList(pageNo, pageSize, stock);

        BaseResponseDTO responseDTO = new BaseResponseDTO(HttpStatus.OK_LAYUI, listStock);
        responseDTO.setCount(stockJdbcDao.countAllStockBase(stock));

        return responseDTO;
    }

    @Override
    public BaseResponseDTO referStockDetail(String stockCode, String startDate, String endDate) {

        List<MyStockData> stockDaoList = stockJdbcDao.referStockData(stockCode, startDate, endDate);
        return new BaseResponseDTO(HttpStatus.OK, stockDaoList);
    }

    @Override
    public BaseResponseDTO referStockTradeDayDetail(String stockCode) {

        return new BaseResponseDTO(HttpStatus.OK, stockJdbcDao.referLatestStockData(stockCode));
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

    public void repairStockData() {

        log.info("开始补齐股票数据...");

        try {
            Runtime.getRuntime().exec(repairStockScript);
        } catch (Exception e) {
            log.error("补齐股票数据失败, ", e);
            return;
        }

        log.info("补齐股票数据结束.");
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
        stockBaseDao.save(entity);
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
    public BaseResponseDTO checkStockExist(String code) {

        MyStockBase stockBase = stockBaseDao.findByStockCode(code);
        return new BaseResponseDTO(HttpStatus.OK, stockBase != null ? true : false);
    }
}
