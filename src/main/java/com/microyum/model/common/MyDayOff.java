package com.microyum.model.common;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;

@Data
@Entity
public class MyDayOff extends BaseModel {

    private Date tradeDate;
    private String remark;
}
