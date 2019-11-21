package com.microyum.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class MyBlogLog extends BaseModel {

    @Column(nullable = false)
    private String ipAddr;

    @Column(nullable = false)
    private String requestPath;
}
