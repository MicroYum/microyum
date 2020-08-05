package com.microyum.model.bond;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
public class MyBondBase extends BaseModel {

    /**
     * 区域
     */
    private String area;
    /**
     * 债券代码
     */
    private String bondCode;
    /**
     * 债券简称
     */
    private String bondName;

    private String stockCode;
    private String stockName;
    /**
     * 转股价
     */
    private BigDecimal transferPrice;
    /**
     * 回售触发价
     */
    private BigDecimal hscfj;
    /**
     * 强赎触发价
     */
    private BigDecimal qscfj;
    /**
     * 上市日
     */
    private Date listingDate;
    /**
     * 转股开始日
     */
    private Date zgStartDate;
    /**
     * 转股结束日
     */
    private Date zgEndDate;
    /**
     * 每年付息日
     */
    private String payDay;
    /**
     * 债券发行总额(亿)
     */
    private BigDecimal bondIssuance;
    /**
     * 信用级别
     */
    private String creditRatings;
    /**
     * 评级机构
     */
    private String ratingAgency;
    /**
     * 利率说明
     */
    private String rateDes;
    /**
     * 回售条款
     */
    private String putsClause;
    /**
     * 赎回条款
     */
    private String callsClause;
    /**
     * 是否观察
     */
    private Byte observe;
}
