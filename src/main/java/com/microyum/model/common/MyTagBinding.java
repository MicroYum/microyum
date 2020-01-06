package com.microyum.model.common;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class MyTagBinding extends BaseModel {

    private Long tagId;
    private Integer category;
    private Long entityId;
}
