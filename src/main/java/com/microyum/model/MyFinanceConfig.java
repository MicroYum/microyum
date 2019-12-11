package com.microyum.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 家庭财务 - 配置表
 */
@Data
@Entity
public class MyFinanceConfig extends BaseModel {

    // TraderAccount表ID
    @Column(nullable = false)
    private String taId;
    // 类型(1: 股票, 2: 债券, 3: 基金, 4: 贵金属)
    @Column(nullable = false)
    private Byte type;
    // 名称
    private String name;
    // 份额
    private String amount;
    // 货币
    private String currency;
}


