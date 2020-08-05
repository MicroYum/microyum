package com.microyum.common.enums;

import com.microyum.common.util.StringUtils;

/**
 * @author syaka.hong
 */
public enum StockStrategyEnum {

    STRATEGY_NO_ADVICE(0, "NO ADVICE"),
    STRATEGY_BUYING(1, "BUYING"),
    STRATEGY_UNDER_VALUE(2, "UNDER VALUE"),
    STRATEGY_MIDDLE_VALUE(3, "MIDDLE VALUE"),
    STRATEGY_OVER_VALUE(4, "OVER VALUE"),
    STRATEGY_SELLING(5, "SELLING");

    private int code;
    private String name;

    StockStrategyEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static StockStrategyEnum of(int code) {
        for (StockStrategyEnum item : StockStrategyEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        throw new IllegalArgumentException("Illegal code");
    }

    public static StockStrategyEnum of(String name) {
        for (StockStrategyEnum item : StockStrategyEnum.values()) {
            if (StringUtils.equals(name, item.getName())) {
                return item;
            }
        }
        throw new IllegalArgumentException("Illegal name");
    }
}
