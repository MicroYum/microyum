package com.microyum.model.blog;

import com.microyum.model.BaseModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class MyAlbumDetail extends BaseModel {

    /**
     * Foreign Key:{@link MyAlbum#id}
     */
    @Column(nullable = false)
    private Long albumId;

    /**
     * 图片路径
     */
    @Column(nullable = false)
    private String path;

    /**
     * 删除: 0; 正常: 1;
     */
    @Column(nullable = false, columnDefinition = "TINYINT")
    private Byte status;
}
