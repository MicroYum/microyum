package com.microyum.model.common;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class MyArticleType extends BaseModel {

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Integer order;
}
