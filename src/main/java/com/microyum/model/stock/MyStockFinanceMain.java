package com.microyum.model.stock;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * 主要指标
 */
@Data
@Entity
public class MyStockFinanceMain extends BaseModel {

    // 股票代码
    private String stockCode;
    // 报告期
    private String reportDate;
    // 营业收入
    private BigDecimal businessIncome;
    // 净利润
    private BigDecimal netProfitAtsopc;
    // 扣非净利润
    private BigDecimal netProfitAfterNrgalAtsolc;
    // 每股收益
    private BigDecimal basicEps;
    // 每股净资产
    private BigDecimal npPerShare;
    // 每股资本公积金
    private BigDecimal capitalReserve;
    // 每股未分配利润
    private BigDecimal undistriProfitPs;
    // 每股经营现金流
    private BigDecimal operateCashFlowPs;
    // 净资产收益率
    private BigDecimal avgRoe;
    // 销售毛利率(%)
    private BigDecimal grossSellingRate;
    // 销售净利率(%)
    private BigDecimal netSellingRate;
    // 资产负债率(%)
    private BigDecimal assetLiabRatio;
}
