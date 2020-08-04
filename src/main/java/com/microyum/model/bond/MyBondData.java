package com.microyum.model.bond;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
public class MyBondData extends BaseModel {

    /**
     * 债券代码
     */
    private String bondCode;
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
     * 成交量
     */
    private BigDecimal volume;
}
