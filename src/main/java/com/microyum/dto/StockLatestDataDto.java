package com.microyum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockLatestDataDto {
    // 股票代码
    private String stockCode;
    // 股票名称
    private String stockName;
    // 交易日
    private Date tradeDate;
    // 开盘价
    private BigDecimal open;
    // 收盘价
    private BigDecimal close;
    // 后复权收盘价
    private BigDecimal hfqClose;
    // 最高价
    private  BigDecimal high;
    // 最低价
    private BigDecimal low;
    // 涨跌额[涨跌幅(%)]
    private String chg;
    // 成交数量(以股为单位)
    private BigDecimal tradeCount;
    // 成交金额(以元为单位)
    private BigDecimal tradeAmount;
}
