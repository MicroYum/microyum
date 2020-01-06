package com.microyum.model.common;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
public class MyTag extends BaseModel {

    /**
     * tag name
     */
    private String name;

    /**
     * Tag类型, 参考TagCategoryEnum(blog, stock, finance ...)
     */
    private String category;

    /**
     * 绑定件数
     */
    @Column(nullable = false, columnDefinition = "BIGINT(20) DEFAULT 0")
    private Long items;

    /**
     * 0：已删除
     * 1：可用
     */
    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer status;
}
