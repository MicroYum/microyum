package com.microyum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogRequestDTO {

    private Long id;
    private String topicImg;
    private String title;
    private String summary;
    private String content;
    private Byte status;
    private Long articleId;
    private Long pageView;
}
