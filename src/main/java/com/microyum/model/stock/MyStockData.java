package com.microyum.model.stock;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 股票数据表(日表)
 */
@Data
@Entity
public class MyStockData extends BaseModel {

    /**
     * 股票代码
     */
    private String stockCode;
    private String area;

    /**
     * 交易日
     */
    private Date tradeDate;

    /**
     * 开盘价
     */
    private BigDecimal open;
    /**
     * 收盘价
     */
    private BigDecimal close;
    /**
     * 最高价
     */
    private  BigDecimal high;
    /**
     * 最低价
     */
    private BigDecimal low;

    /**
     * 涨跌幅(%)
     */
    private BigDecimal percent;
    /**
     * 涨跌额
     */
    private BigDecimal chg;

    /**
     * 成交数量(以股为单位)
     */
    private BigDecimal tradeCount;
    /**
     * 成交金额(以元为单位)
     */
    private BigDecimal tradeAmount;
}
