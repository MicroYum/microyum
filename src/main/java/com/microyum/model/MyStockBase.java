package com.microyum.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

/**
 * 基础表
 */
@Data
@Entity
public class MyStockBase extends BaseModel {

    // 股票代码
    private String stockCode;
    // 股票名字
    private String stockName;
    // 地域（SH表示上海，SZ表示深圳）
    private String area;
    // 上市日期
    private Date listingDate;
    // 总股本(亿)
    private Double totalCapital;
    // 流通股本(亿)
    private Double circulationCapital;
    // 公司简介
    @Column(nullable = false, columnDefinition = "TEXT")
    private String introduction;
    // 列表排序序号
    private Integer listSort;
    // 是否列入观察
    private Byte observe;
    // 日数据开始日期
    private Date dailyDate;
    // 明细数据开始日期
    private Date detailDate;
    // 类型(1:股票; 2:可转债; 3:国债; 4:企业债;)
    private String type;
}
