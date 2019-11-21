package com.microyum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String email;
    private Byte locked;
    private String name;
    private String openId;
    private Long parentId;
    private String password;
    private String telephone;
    private String unionId;

}
