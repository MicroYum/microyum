package com.microyum.model.common;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class MyMailTemplate extends BaseModel {

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mailBody;
}
