package com.microyum.model.common;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
public class MyRole extends BaseModel {

    @Column(nullable = false)
    private String name;
}
