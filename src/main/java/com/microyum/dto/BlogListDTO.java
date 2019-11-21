package com.microyum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogListDTO {

    private Long id;
    private String title;
    private String status;
    private String article;
    private Long pageView;
    private String createTime;
}
