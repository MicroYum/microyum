package com.microyum.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
public class MyUserRole extends BaseModel {

    /**
     * Foreign Key:{@link MyUser#id}
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * Foreign Key:{@link MyRole#id}
     */
    @Column(nullable = false)
    private Long roleId;

    @Column(nullable = false, columnDefinition = "BIGINT(20) DEFAULT 1")
    private Long status;
}
