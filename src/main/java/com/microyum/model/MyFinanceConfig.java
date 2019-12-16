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
    private Long taId;
    // 类型，参考：FinanceTypeEnum
    @Column(nullable = false)
    private String type;
    // 名称
    private String name;
    // 份额
    private String amount;
    // 货币
    private String currency;
}


