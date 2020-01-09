package com.microyum.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BuyingStockBO {

    private String stockCode;
    private String area;
    private String stockName;
    private BigDecimal latestPrice;
    private BigDecimal latestHfqPrice;
    private BigDecimal buyingMin;
    private BigDecimal buyingMax;
    private BigDecimal underMin;
    private BigDecimal underMax;
    private Integer tradeCount;
    private BigDecimal priceRate;
    private BigDecimal volumeRate;
}
