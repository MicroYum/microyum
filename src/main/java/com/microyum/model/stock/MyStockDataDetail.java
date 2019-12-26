package com.microyum.model.stock;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 股票数据表(分钟表)
 */
@Data
@Entity
public class MyStockDataDetail extends BaseModel {

    // 股票代码
    private String symbol;
    // 交易日时
    private Date tradeDatetime;
    // 当前价
    private BigDecimal current;
    // 成交数量(以股为单位)
    private BigDecimal tradeCount;
    // 成交金额(以元为单位)
    private BigDecimal tradeAmount;
}
