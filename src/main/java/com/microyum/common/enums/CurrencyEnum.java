package com.microyum.common.enums;

import com.microyum.common.util.StringUtils;

public enum CurrencyEnum {

    CURRENCY_CNY(1, "CNY"),
    CURRENCY_USD(2, "USD");


    private int code;
    private String name;

    CurrencyEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static CurrencyEnum of(int code) {
        for (CurrencyEnum item : CurrencyEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        throw new IllegalArgumentException("Illegal code");
    }

    public static CurrencyEnum of(String name) {
        for (CurrencyEnum item : CurrencyEnum.values()) {
            if (StringUtils.equals(name, item.getName())) {
                return item;
            }
        }
        throw new IllegalArgumentException("Illegal name");
    }
}
