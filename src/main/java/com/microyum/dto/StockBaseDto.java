package com.microyum.dto;

import lombok.Data;

@Data
public class StockBaseDto {

    // 股票代码
    private String stockCode;
    // 股票名字
    private String stockName;
    // 地域（SH表示上海，SZ表示深圳）
    private String area;
    // 上市日期
    private String listingDate;
    // 总股本(亿)
    private String totalCapital;
    // 流通股本(亿)
    private String circulationCapital;
    // 公司簡介
    private String introduction;
    // 类型(1:股票; 2:可转债; 3:国债; 4:企业债;)
    private String type;
    // 汉字首字母
    private String initials;
}
