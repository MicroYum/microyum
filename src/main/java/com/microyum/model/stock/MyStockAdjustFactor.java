package com.microyum.model.stock;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 复权因子表
 *
 * @author syaka.hong
 */
@Data
@Entity
public class MyStockAdjustFactor extends BaseModel {

    private String area;
    private String stockCode;

    /**
     * 除权除息日期
     */
    private Date dividOperateDate;

    /**
     * 向前复权因子
     */
    private BigDecimal foreAdjustFactor;

    /**
     * 向后复权因子
     */
    private BigDecimal backAdjustFactor;
}
