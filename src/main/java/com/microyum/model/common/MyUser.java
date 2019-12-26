package com.microyum.model.common;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
public class MyUser extends BaseModel {

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String nickName;

    @Column(nullable = false, length = 80)
    private String password;

    @Column(nullable = true, length = 50)
    private String email;

    @Column(nullable = true, length = 20)
    private String telephone;

    @Column(nullable = true, length = 50)
    private String unionId;

    @Column(nullable = true, length = 50)
    private String openId;

    @Column(nullable = true)
    private Long parentId;

    @Column(nullable = false, columnDefinition = "bit(1) DEFAULT false")
    private Boolean locked;

    @Column(nullable = false, length = 80)
    private String salt;

    @Column(nullable = false, columnDefinition = "tinyint default 1")
    private Byte notification;
}
