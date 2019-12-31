package com.microyum.dto;

import lombok.Data;

@Data
public class StockBaseListDto {

    private String area;
    // 股票代码
    private String stockCode;
    // 股票名字
    private String stockName;
    // 类型(1:股票; 2:可转债; 3:国债; 4:企业债;)
    private String type;
    // 流通股本/縂股本(億)
    private String capital;
    // 記錄開始日期
    private String startDate;
    // 推薦等級, 參考StockStrategyEnum
    private String strategy;
    // 等級日期
    private String strategyDate;
}
