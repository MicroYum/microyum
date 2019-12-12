package com.microyum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraderAccountDto {

    private Long id;
    private Long userId;
    private String userName;
    private String nickName;
    private String trader;
    private String account;

}
