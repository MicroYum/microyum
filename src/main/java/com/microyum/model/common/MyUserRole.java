package com.microyum.model.common;

import com.microyum.model.BaseModel;
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

}
