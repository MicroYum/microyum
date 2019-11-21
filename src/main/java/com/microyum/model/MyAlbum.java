package com.microyum.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 足迹实体类
 */
@Data
@Entity
public class MyAlbum extends BaseModel {

    /**
     * Foreign Key:{@link MyUser#id}
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 概述
     */
    @Column(nullable = false)
    private String summary;

    /**
     * 封面图片路径
     */
    @Column(nullable = false)
    private String cover;

    /**
     * 删除: 0; 正常: 1; 置顶: 2;
     */
    @Column(nullable = false, columnDefinition = "TINYINT")
    private Byte status;
}
