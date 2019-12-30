package com.microyum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetAllocationDto {

    private Long id;
    private String assetType;
    private String assetName;
    private String amount;
    private String currency;

    private Long taId;
    private String trader;
    private String account;

    private Long uid;
    private String userName;
    private String nickName;

    private String area;
    private String stockCode;
}
