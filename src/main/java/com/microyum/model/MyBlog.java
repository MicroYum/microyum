package com.microyum.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class MyBlog extends BaseModel {

    /**
     * Foreign Key:{@link MyUser#id}
     */
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String topicImg;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 删除: 0; 正常: 1; 置顶: 2; 草稿: 3;
     */
    @Column(nullable = false, columnDefinition = "TINYINT")
    private Byte status;

    /**
     * Foreign Key:{@link MyArticleType#id}
     */
    @Column(nullable = false)
    private Long articleId;

    @Column(nullable = false, columnDefinition = "BIGINT(20) DEFAULT 0")
    private Long pageView;
}
