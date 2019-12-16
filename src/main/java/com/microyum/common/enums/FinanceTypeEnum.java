package com.microyum.common.enums;

import com.microyum.common.util.StringUtils;

public enum FinanceTypeEnum {

    FINANCE_TYPE_STOCK(1, "Stock"),
    FINANCE_TYPE_BOND(2, "Bond"),
    FINANCE_TYPE_FUND(3, "Fund"),
    FINANCE_TYPE_PRECIOUS_METALS(4, "Precious Metals");

    private int code;
    private String name;

    FinanceTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static FinanceTypeEnum of(int code) {
        for (FinanceTypeEnum item : FinanceTypeEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        throw new IllegalArgumentException("Illegal code");
    }

    public static FinanceTypeEnum of(String name) {
        for (FinanceTypeEnum item : FinanceTypeEnum.values()) {
            if (StringUtils.equals(name, item.getName())) {
                return item;
            }
        }
        throw new IllegalArgumentException("Illegal name");
    }
}
