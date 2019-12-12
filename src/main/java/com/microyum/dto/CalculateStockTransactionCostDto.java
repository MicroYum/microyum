package com.microyum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 计算股票交易成本DTO类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateStockTransactionCostDto {

    // --------------------------------------------------
    // Input
    // --------------------------------------------------
    // 交易对象(1:上海A股; 2:上海B股; 3:深圳A股; 4:深圳B股;)
    private String tradingObject;
    // 股票价格(元)
    private BigDecimal stockPrice;
    // 股票成交价(元)
    private BigDecimal stockObjectPrice;
    // 股票成交量(股)
    private BigDecimal stockCount;
    // 佣金比例(%)
    private BigDecimal commissionRate;

    // --------------------------------------------------
    // Output
    // --------------------------------------------------
    // 印花税
    private String stampDuty;
    // 佣金
    private String commission;
    // 监管费
    private String supervisionFee;
    // 过户费
    private String transferFee;
    // 经手费
    private String exchangeFee;
    // 税费合计
    private String totalTax;
    // 占额交易(%)
    private String shareTrading;
    // 盈利(元)
    private String profit;


    @Override
    public String toString() {
        return "每股买入价 = '" + stockPrice + "', 卖出价 = '" + stockObjectPrice + "', 交易数量 = '" + stockCount + "', 交易成本 = {" +
                "印花税 = '" + stampDuty + "', 佣金 = '" + commission + "', 监管费 = '" + supervisionFee + "', 过户费 = '" + transferFee +
                "', 经手费 = '" + exchangeFee + "', 税费合计 = '" + totalTax + "', 占额交易(%) = '" + shareTrading + "%'}, 总盈利 = '" + profit + "'";
    }
}
