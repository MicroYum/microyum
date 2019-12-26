package com.microyum.model.stock;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
public class MyStockDailyStrategy extends BaseModel {

    public String stockCode;
    public Date tradeDate;

    // 策略，参考: StockStrategyEnum
    public Integer strategy;

    // 最新收盘价格
    public BigDecimal latestPrice;
    public BigDecimal latestHfqPrice;

    // 建议买入高低值
    public BigDecimal buyingMin;
    public BigDecimal buyingMax;

    // 低估高低值
    public BigDecimal underMin;
    public BigDecimal underMax;

    // 中间区域高低值
    public BigDecimal middleMin;
    public BigDecimal middleMax;

    // 高估高低值
    public BigDecimal overMin;
    public BigDecimal overMax;

    // 建议卖出高低值
    public BigDecimal sellingMin;
    public BigDecimal sellingMax;

    // 价格比率
    public BigDecimal priceRate;
    // 交易量比率
    public BigDecimal volumeRate;
    // 分红率
    public BigDecimal dividendRate;

    // 交易天数
    public Integer tradeCount;

}
