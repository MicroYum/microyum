package com.microyum.model.stock;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class MyStockTurnoverStrategy extends BaseModel {

    private String area;
    private String stockCode;
    private Double lowerTurnover;
    private Double higherTurnover;
    private Integer dateRange;
}
