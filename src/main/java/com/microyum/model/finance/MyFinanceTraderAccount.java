package com.microyum.model.finance;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 用户Id、交易渠道、账号对应关系
 */
@Entity
@Data
public class MyFinanceTraderAccount extends BaseModel {

    // 用户ID
    @Column(nullable = false)
    private Long uid;
    // 交易渠道
    @Column(nullable = false)
    private String trader;
    // 账号
    @Column(nullable = false)
    private String account;
}
