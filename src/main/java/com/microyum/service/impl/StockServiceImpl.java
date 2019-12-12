package com.microyum.service.impl;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.dao.MyStockDao;
import com.microyum.dto.CalculateStockTransactionCostDto;
import com.microyum.model.MyStockBase;
import com.microyum.model.MyStockData;
import com.microyum.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

@Service
@Slf4j
public class StockServiceImpl implements StockService {

    @Autowired
    private MyStockDao stockDao;

    @Value("${python.script.repair.stock.hfqdata}")
    private String repairStockScript;

    @Override
    public BaseResponseDTO referStockList(int pageNo, int pageSize, String stock) {

        List<MyStockBase> listStock = stockDao.referStockList(pageNo, pageSize, stock);

        BaseResponseDTO responseDTO = new BaseResponseDTO(HttpStatus.OK_LAYUI, listStock);
        responseDTO.setCount(stockDao.countAllStockBase(stock));

        return responseDTO;
    }

    @Override
    public BaseResponseDTO referStockDetail(String stockCode, String startDate, String endDate) {

        List<MyStockData> stockDaoList = stockDao.referStockData(stockCode, startDate, endDate);
        return new BaseResponseDTO(HttpStatus.OK, stockDaoList);
    }

    @Override
    public BaseResponseDTO referStockTradeDayDetail(String stockCode) {

        return new BaseResponseDTO(HttpStatus.OK, stockDao.referLatestStockData(stockCode));
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

    public static void main(String[] args) {
        CalculateStockTransactionCostDto dto;

        BigDecimal stockPrice = BigDecimal.valueOf(5);
        BigDecimal stockObjectPrice = BigDecimal.valueOf(5.01);
        BigDecimal baseCount = BigDecimal.valueOf(1000);

        StockServiceImpl stockService = new StockServiceImpl();


        for (int i = 1; i <= 10; i++) {
            dto = new CalculateStockTransactionCostDto();
            dto.setTradingObject("1");
            dto.setStockPrice(stockPrice);
            dto.setStockObjectPrice(stockObjectPrice);
            dto.setStockCount(baseCount.multiply(BigDecimal.valueOf(i)));
            dto.setCommissionRate(BigDecimal.valueOf(0.025));
            stockService.calculateStockTransactionCost(dto);

            System.out.println(dto);
        }
    }
}
