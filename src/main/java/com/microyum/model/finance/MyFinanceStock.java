package com.microyum.model.finance;

import java.util.Date;

/**
 * 家庭财务 - 股票（每天统计一次）
 */
public class MyFinanceStock {

    // 券商
    private String trader;
    // 账号
    private String account;
    // 地域（SH表示上海，SZ表示深圳）
    private String area;
    // 股票代码
    private String stockCode;
    // 股票名称
    private String stockName;
    // 份额
    private String amount;
    // 股价
    private String price;
    // 市值
    private String total;
    // 日期
    private Date tradeDate;
}
