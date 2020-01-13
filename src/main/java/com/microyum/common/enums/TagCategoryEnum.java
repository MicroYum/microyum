package com.microyum.common.enums;

import com.microyum.common.util.StringUtils;

public enum TagCategoryEnum {

    TAG_CATEGORY_STOCK(1, "Stock"),
    TAG_CATEGORY_BOND(2, "Bond"),
    TAG_CATEGORY_FINANCE(3, "Finance"),
    TAG_CATEGORY_BLOG(4, "Blog");

    private int code;
    private String name;

    TagCategoryEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static TagCategoryEnum of(int code) {
        for (TagCategoryEnum item : TagCategoryEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        throw new IllegalArgumentException("Illegal code");
    }

    public static TagCategoryEnum of(String name) {
        for (TagCategoryEnum item : TagCategoryEnum.values()) {
            if (StringUtils.equals(name, item.getName())) {
                return item;
            }
        }
        throw new IllegalArgumentException("Illegal name");
    }
}
