package com.microyum.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockStrategyBO {

    private String stockCode;
    private String stockName;
    private BigDecimal current;
    private BigDecimal hfqCurrent;
    private BigDecimal high;
    private BigDecimal low;
    private boolean isBuy;
    private boolean isBuyObserve;
    private boolean isSell;
    private boolean isSellObserve;

    private String tradeDate;

    // 有记录的交易天数
    private Integer date;

    // 比当前价格低的天数
    private Integer lowPriceDate;
    // 比当前交易量低的天数
    private Integer lowVolumeDate;

    // 比当前价格高的天数
    private Integer highPriceDate;
    // 比当前交易量高的天数
    private Integer highVolumeDate;
}
