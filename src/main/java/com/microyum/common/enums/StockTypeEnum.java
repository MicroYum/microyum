package com.microyum.common.enums;

import com.microyum.common.util.StringUtils;

public enum StockTypeEnum {

    STOCK_TYPE_STOCK(1, "Stock"),
    STOCK_TYPE_CONVERTIBLE_BOND(2, "Convertible Bond"),
    STOCK_TYPE_NATIONAL_DEBT(3, "National Debt"),
    STOCK_TYPE_ENTERPRISE_BOND(4, "Enterprise Bond");

    private int code;
    private String name;

    StockTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static StockTypeEnum of(int code) {
        for (StockTypeEnum item : StockTypeEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        throw new IllegalArgumentException("Illegal code");
    }

    public static StockTypeEnum of(String name) {
        for (StockTypeEnum item : StockTypeEnum.values()) {
            if (StringUtils.equals(name, item.getName())) {
                return item;
            }
        }
        throw new IllegalArgumentException("Illegal name");
    }
}
