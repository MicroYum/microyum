package com.microyum.model.stock;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
public class MyStockDailyStrategy extends BaseModel {

    private String area;
    private String stockCode;
    private Date tradeDate;

    // 策略，参考: StockStrategyEnum
    private Integer strategy;

    // 最新收盘价格
    private BigDecimal latestPrice;
    private BigDecimal latestHfqPrice;

    // 建议买入高低值
    private BigDecimal buyingMin;
    private BigDecimal buyingMax;

    // 低估高低值
    public BigDecimal underMin;
    public BigDecimal underMax;

    // 中间区域高低值
    private BigDecimal middleMin;
    private BigDecimal middleMax;

    // 高估高低值
    private BigDecimal overMin;
    private BigDecimal overMax;

    // 建议卖出高低值
    private BigDecimal sellingMin;
    private BigDecimal sellingMax;

    // 价格比率
    private BigDecimal priceRate;
    // 交易量比率
    private BigDecimal volumeRate;
    // 分红率
    private BigDecimal dividendRate;

    // 交易天数
    public Integer tradeCount;
}
