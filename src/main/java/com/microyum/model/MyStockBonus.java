package com.microyum.model;

import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 股票分红表
 */
@Data
@Entity
public class MyStockBonus extends BaseModel {

    // 股票代码
    private String stockCode;
    // 分红金额
    private BigDecimal bonus;
    // 分红日期
    private Date bonusDate;
    // 配股
    private BigDecimal rationedShares;
    // 配股日期
    private Date rationedSharesDate;
    // 派股
    private BigDecimal sentStocks;
    // 派股日期
    private Date sentStocksDate;
}
