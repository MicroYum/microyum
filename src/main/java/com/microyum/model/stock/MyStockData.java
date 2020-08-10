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
     * 后复权收盘价
     */
    private BigDecimal hfqClose;
    /**
     * 最高价
     */
    private BigDecimal high;
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
    /**
     * 换手率
     * [指定交易日的成交量(股)/指定交易日的股票的流通股总股数(股)]*100%
     */
    private BigDecimal turn;
    /**
     * 滚动市盈率
     * (指定交易日的股票收盘价/指定交易日的每股盈余TTM)=(指定交易日的股票收盘价*截至当日公司总股本)/归属母公司股东净利润TTM
     */
    private BigDecimal peTtm;
    /**
     * 市净率
     * (指定交易日的股票收盘价/指定交易日的每股净资产)=总市值/(最近披露的归属母公司股东的权益-其他权益工具)
     */
    private BigDecimal pbMrq;
    /**
     * 滚动市销率
     * (指定交易日的股票收盘价/指定交易日的每股销售额)=(指定交易日的股票收盘价*截至当日公司总股本)/营业总收入TTM
     */
    private BigDecimal psTtm;
    /**
     * 滚动市现率
     * (指定交易日的股票收盘价/指定交易日的每股现金流TTM)=(指定交易日的股票收盘价*截至当日公司总股本)/现金以及现金等价物净增加额TTM
     */
    private BigDecimal pcfNcfTtm;
}
