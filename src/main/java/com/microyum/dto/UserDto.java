package com.microyum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PrivateKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String locked;
    private String name;
    private String nickName;
    private String openId;
    private String parentId;
    private String password;
    private String telephone;
    private String unionId;
    private String createDate;
    private String lastUpdateDate;
    private Long roleId;
    private String roleName;

}
